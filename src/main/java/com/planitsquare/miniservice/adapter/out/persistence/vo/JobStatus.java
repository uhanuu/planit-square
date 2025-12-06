package com.planitsquare.miniservice.adapter.out.persistence.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 동기화 Job의 상태를 나타내는 Enum.
 *
 * <p>Job의 실행 상태를 추적하여 동시성 제어 및 부분 실패 처리를 지원합니다.
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum JobStatus {
  /**
   * 실행 중 상태.
   *
   * <p>Job이 현재 실행되고 있음을 나타냅니다. 이 상태의 Job이 존재하면 새로운 Job 시작을 차단합니다.
   */
  RUNNING("실행 중"),

  /**
   * 완료 상태.
   *
   * <p>모든 작업이 성공적으로 완료되었음을 나타냅니다.
   */
  COMPLETED("완료"),

  /**
   * 실패 상태.
   *
   * <p>모든 작업이 실패했음을 나타냅니다.
   */
  FAILED("실패");

  private final String displayName;

  /**
   * 실행 중 상태인지 확인합니다.
   *
   * @return 실행 중 상태이면 {@code true}
   * @since 1.0
   */
  public boolean isRunning() {
    return this == RUNNING;
  }

  /**
   * 완료 상태인지 확인합니다.
   *
   * @return 완료 상태이면 {@code true}
   * @since 1.0
   */
  public boolean isCompleted() {
    return this == COMPLETED;
  }

  /**
   * 실패 상태인지 확인합니다.
   *
   * @return 실패 상태이면 {@code true}
   * @since 1.0
   */
  public boolean isFailed() {
    return this == FAILED;
  }
}
