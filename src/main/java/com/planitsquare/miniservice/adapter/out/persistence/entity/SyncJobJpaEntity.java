package com.planitsquare.miniservice.adapter.out.persistence.entity;

import com.planitsquare.miniservice.adapter.out.persistence.vo.JobStatus;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 동기화 Job을 관리하는 JPA Entity.
 *
 * <p>Spring Batch 스타일의 Job 관리를 제공하여 전체 동기화 작업을 추적하고,
 * 동시 실행 방지 및 부분 실패 처리를 지원합니다.
 *
 * <p>Aggregate Root 역할을 수행하며, 여러 {@link SyncHistoryJpaEntity}를 관리합니다.
 *
 * @since 1.0
 */
@Entity
@Table(name = "sync_job")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncJobJpaEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "job_id")
  private Long id;

  /**
   * 실행 타입 (자동/수동, 초기 적재/정기 배치 등).
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "execution_type", nullable = false, length = 30)
  private SyncExecutionType executionType;

  /**
   * Job 상태 (RUNNING, COMPLETED, PARTIAL_FAILURE, FAILED).
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private JobStatus status;

  /**
   * Job 시작 시간.
   */
  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  /**
   * Job 종료 시간.
   */
  @Column(name = "end_time")
  private LocalDateTime endTime;

  /**
   * 전체 작업 수.
   */
  @Column(name = "total_tasks")
  private Integer totalTasks;

  /**
   * 성공한 작업 수.
   */
  @Column(name = "success_count")
  private Integer successCount;

  /**
   * 실패한 작업 수.
   */
  @Column(name = "failure_count")
  private Integer failureCount;

  /**
   * 낙관적 락을 위한 버전 필드.
   *
   * <p>동시성 제어를 위해 사용됩니다.
   */
  @Version
  @Column(name = "version")
  private Long version;

  @Builder
  private SyncJobJpaEntity(
      Long id,
      SyncExecutionType executionType,
      JobStatus status,
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer totalTasks,
      Integer successCount,
      Integer failureCount,
      Long version
  ) {
    this.id = id;
    this.executionType = executionType;
    this.status = status;
    this.startTime = startTime;
    this.endTime = endTime;
    this.totalTasks = totalTasks;
    this.successCount = successCount;
    this.failureCount = failureCount;
    this.version = version;
  }

  /**
   * 새로운 동기화 Job을 시작합니다.
   *
   * <p>초기 상태는 {@link JobStatus#RUNNING}이며, 시작 시간이 자동으로 설정됩니다.
   *
   * @param executionType 실행 타입, {@code null}이 아니어야 함
   * @return 생성된 {@link SyncJobJpaEntity}
   * @throws IllegalArgumentException executionType이 {@code null}이거나 totalTasks가 음수인 경우
   * @since 1.0
   */
  public static SyncJobJpaEntity start(SyncExecutionType executionType) {
    Assert.notNull(executionType, "Execution type must not be null");

    return SyncJobJpaEntity.builder()
        .executionType(executionType)
        .status(JobStatus.RUNNING)
        .startTime(LocalDateTime.now())
        .build();
  }


  /**
   * Job을 통계와 함께 완료 처리합니다.
   *
   * <p>성공/실패 카운트를 기반으로 최종 상태를 결정합니다:
   * <ul>
   *   <li>모든 작업 성공: {@link JobStatus#COMPLETED}</li>
   *   <li>일부 성공, 일부 실패: {@link JobStatus#PARTIAL_SUCCESS}</li>
   *   <li>모든 작업 실패: {@link JobStatus#FAILED}</li>
   * </ul>
   *
   * @param endTime 종료 시간
   * @param totalTasks 전체 작업 수
   * @param successCount 성공한 작업 수
   * @param failureCount 실패한 작업 수
   * @since 1.0
   */
  public void completeWithStats(
      LocalDateTime endTime,
      int totalTasks,
      int successCount,
      int failureCount
  ) {
    this.endTime = endTime;
    this.totalTasks = totalTasks;
    this.successCount = successCount;
    this.failureCount = failureCount;

    // 상태 결정
    if (failureCount == 0) {
      this.status = JobStatus.COMPLETED;
    } else if (successCount == 0) {
      this.status = JobStatus.FAILED;
    } else {
      this.status = JobStatus.PARTIAL_SUCCESS;
    }
  }


  /**
   * Job이 실행 중인지 확인합니다.
   *
   * @return 실행 중이면 {@code true}
   * @since 1.0
   */
  public boolean isRunning() {
    return this.status.isRunning();
  }

  /**
   * Job이 완료되었는지 확인합니다.
   *
   * @return 완료 상태이면 {@code true}
   * @since 1.0
   */
  public boolean isCompleted() {
    return this.status.isCompleted();
  }

  /**
   * Job이 실패했는지 확인합니다.
   *
   * @return 실패 상태이면 {@code true}
   * @since 1.0
   */
  public boolean isFailed() {
    return this.status.isFailed();
  }

}
