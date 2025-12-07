package com.planitsquare.miniservice.application.exception;

/**
 * RUNNING 상태의 Job이 이미 존재할 때 발생하는 예외.
 *
 * <p>동시성 제어를 위해 사용되며, 새로운 Job 시작을 차단합니다.
 *
 * @since 1.0
 */
public class JobAlreadyRunningException extends RuntimeException {

  /**
   * 기본 메시지로 예외를 생성합니다.
   */
  public JobAlreadyRunningException() {
    super("이미 실행 중인 Job이 존재합니다. 현재 Job이 완료될 때까지 새로운 Job을 시작할 수 없습니다.");
  }

  /**
   * 사용자 정의 메시지로 예외를 생성합니다.
   *
   * @param message 예외 메시지
   */
  public JobAlreadyRunningException(String message) {
    super(message);
  }

  /**
   * 사용자 정의 메시지와 원인으로 예외를 생성합니다.
   *
   * @param message 예외 메시지
   * @param cause 원인
   */
  public JobAlreadyRunningException(String message, Throwable cause) {
    super(message, cause);
  }
}
