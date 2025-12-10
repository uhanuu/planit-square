package com.planitsquare.miniservice.application.exception;

/**
 * 외부 API 호출 실패 예외.
 *
 * <p>외부 시스템과의 통신 중 복구 불가능한 오류가 발생했을 때 발생합니다.
 * 재시도를 모두 소진한 후에도 실패한 경우 이 예외가 발생합니다.
 *
 * @since 1.0
 */
public class ExternalApiException extends RuntimeException {

  /**
   * 사용자 정의 메시지와 원인으로 예외를 생성합니다.
   *
   * @param message 예외 메시지
   * @param cause 원인
   */
  public ExternalApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
