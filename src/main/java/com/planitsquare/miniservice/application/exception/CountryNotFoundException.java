package com.planitsquare.miniservice.application.exception;

/**
 * 국가를 찾을 수 없을 때 발생하는 예외.
 *
 * <p>존재하지 않는 국가 코드로 조회 또는 삭제를 시도할 때 발생합니다.
 *
 * @since 1.0
 */
public class CountryNotFoundException extends RuntimeException {

  /**
   * 기본 메시지로 예외를 생성합니다.
   */
  public CountryNotFoundException() {
    super("해당하는 국가 코드가 존재하지 않습니다.");
  }

  /**
   * 사용자 정의 메시지로 예외를 생성합니다.
   *
   * @param message 예외 메시지
   */
  public CountryNotFoundException(String message) {
    super(message);
  }

  /**
   * 사용자 정의 메시지와 원인으로 예외를 생성합니다.
   *
   * @param message 예외 메시지
   * @param cause 원인
   */
  public CountryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
