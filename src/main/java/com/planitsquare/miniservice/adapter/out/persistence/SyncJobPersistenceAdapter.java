package com.planitsquare.miniservice.adapter.out.persistence;

import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncJobJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncHistoryJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncJobJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.vo.JobStatus;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.exception.JobAlreadyRunningException;
import com.planitsquare.miniservice.application.port.out.SyncJobPort;
import com.planitsquare.miniservice.common.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 동기화 Job 관리를 위한 Persistence Adapter.
 *
 * <p>SyncJobPort 구현체로, Job의 생명주기를 관리하고 동시성 제어를 제공합니다.
 *
 * @since 1.0
 */
@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class SyncJobPersistenceAdapter implements SyncJobPort {

  private final SyncJobJpaRepository syncJobJpaRepository;

  /**
   * 새로운 동기화 Job을 시작합니다.
   *
   * <p>독립적인 트랜잭션(REQUIRES_NEW)으로 실행되어, 메인 작업과 별도로 커밋됩니다.
   * RUNNING 상태의 Job이 이미 존재하면 {@link JobAlreadyRunningException}을 발생시킵니다.
   *
   * @param executionType 실행 타입
   * @return 생성된 Job의 ID
   * @throws JobAlreadyRunningException RUNNING 상태의 Job이 이미 존재하는 경우
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Long startJob(SyncExecutionType executionType) {
    // 동시성 제어: RUNNING 상태의 Job 존재 여부 확인
    if (syncJobJpaRepository.existsByStatus(JobStatus.RUNNING)) {
      log.warn("Job 시작 실패 - 이미 실행 중인 Job이 존재합니다. ExecutionType: {}", executionType);
      throw new JobAlreadyRunningException();
    }

    SyncJobJpaEntity job = SyncJobJpaEntity.start(executionType);
    SyncJobJpaEntity savedJob = syncJobJpaRepository.save(job);

    log.info("Job 시작 - Job ID: {}, ExecutionType: {}", savedJob.getId(), executionType);

    return savedJob.getId();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void completeJob(Long jobId) {
    final SyncJobJpaEntity job = syncJobJpaRepository.findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

    LocalDateTime endTime = LocalDateTime.now();
    job.completeWithStats(endTime, 0, 0, 0);

    long durationMillis = Duration.between(job.getStartTime(), endTime).toMillis();
    log.info("Job 완료 - Job ID: {}, 상태: {}, 실행시간: {}ms",
        jobId, job.getStatus().getDisplayName(), durationMillis);
  }

  /**
   * Job을 통계 정보와 함께 완료 처리합니다.
   *
   * <p>독립적인 트랜잭션(REQUIRES_NEW)으로 실행되어, 메인 작업과 별도로 커밋됩니다.
   * 성공/실패 카운트를 기반으로 최종 상태를 자동으로 결정합니다.
   *
   * @param jobId Job ID
   * @param totalTasks 전체 작업 수
   * @param successCount 성공한 작업 수
   * @param failureCount 실패한 작업 수
   * @since 1.0
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void completeJobWithStats(Long jobId, int totalTasks, int successCount, int failureCount) {
    final SyncJobJpaEntity job = syncJobJpaRepository.findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

    LocalDateTime endTime = LocalDateTime.now();
    job.completeWithStats(endTime, totalTasks, successCount, failureCount);

    long durationMillis = Duration.between(job.getStartTime(), endTime).toMillis();
    log.info("Job 완료 - Job ID: {}, 상태: {}, 전체: {}, 성공: {}, 실패: {}, 실행시간: {}ms",
        jobId, job.getStatus().getDisplayName(), totalTasks, successCount, failureCount, durationMillis);
  }

  @Override
  public boolean isInitialSystemLoad() {
    return !syncJobJpaRepository.existsByIdIsNotNull();
  }

  /**
   * 현재 실행 중인 Job이 있는지 확인합니다.
   *
   * <p>RUNNING 상태의 Job이 존재하는지 확인합니다.
   * 삭제 작업 등 데이터 무결성이 중요한 작업 전에 호출하여 동시성을 제어합니다.
   *
   * @return 실행 중인 Job이 존재하면 true, 없으면 false
   * @since 1.0
   */
  @Override
  public boolean hasRunningJob() {
    return syncJobJpaRepository.existsByStatus(JobStatus.RUNNING);
  }
}
