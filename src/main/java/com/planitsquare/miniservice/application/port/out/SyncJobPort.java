package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.exception.JobAlreadyRunningException;

/**
 * 동기화 Job 관리를 위한 Port 인터페이스.
 *
 * <p>Job의 시작, 성공/실패 카운트 증가, 완료 등의 작업을 처리합니다.
 * 동시성 제어 및 부분 실패 처리를 지원합니다.
 *
 * @since 1.0
 */
public interface SyncJobPort {

  /**
   * 새로운 동기화 Job을 시작합니다.
   *
   * <p>RUNNING 상태의 Job이 이미 존재하면 {@link JobAlreadyRunningException}을 발생시킵니다.
   *
   * @param executionType 실행 타입
   * @return 생성된 Job의 ID
   * @throws JobAlreadyRunningException RUNNING 상태의 Job이 이미 존재하는 경우
   * @since 1.0
   */
  Long startJob(SyncExecutionType executionType);

  /**
   * Job을 완료 처리합니다.
   *
   * <p>성공/실패 카운트를 기반으로 최종 상태를 결정하고, 종료 시간을 설정합니다.
   *
   * @param jobId Job ID
   * @since 1.0
   */
  void completeJob(Long jobId);

  /**
   * 실행 했던 Job이 있는지 판단합니다.
   *
   * @return 실행했던 Job이 존재하는 경우 false를 반환합니다.
   */
  boolean isInitialSystemLoad();
}
