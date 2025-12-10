package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.exception.JobAlreadyRunningException;
import com.planitsquare.miniservice.application.port.out.SyncJobPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 동기화 Job 상태를 검증하는 Validator.
 *
 * <p>Job 실행 전에 실행 중인 Job이 있는지 확인하여 데이터 무결성을 보장합니다.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncJobValidator {

  private final SyncJobPort syncJobPort;

  /**
   * 현재 실행 중인 Job이 없는지 검증합니다.
   *
   * @throws JobAlreadyRunningException 실행 중인 Job이 존재하는 경우
   * @since 1.0
   */
  public void validateNoRunningJob() {
    if (syncJobPort.hasRunningJob()) {
      log.warn("Job 실행 실패 - 이미 실행 중인 Job이 존재합니다.");
      throw new JobAlreadyRunningException();
    }
  }
}
