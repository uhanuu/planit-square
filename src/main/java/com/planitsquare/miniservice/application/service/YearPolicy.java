package com.planitsquare.miniservice.application.service;

import lombok.Getter;

/**
 * 연도 정책을 정의하는 Enum.
 *
 * <p>공휴일 시스템에서 사용하는 연도 범위 및 최소 연도 정책을 관리합니다.
 * 정책에 따라 연도 범위 계산 및 검증 기능을 제공합니다.
 *
 * @since 1.0
 */
@Getter
public enum YearPolicy {

  /**
   * 연도 범위 길이 (기본값: 5년).
   */
  RANGE_LENGTH(5),

  /**
   * 최소 허용 연도 (기본값: 2000년).
   */
  MIN_YEAR(2000);

  private final int value;

  YearPolicy(int value) {
    this.value = value;
  }

  /**
   * 입력된 연도가 최소 허용 연도 이상인지 확인합니다.
   *
   * @param year 확인할 연도
   * @return 최소 허용 연도(2000년) 이상이면 {@code true}, 그렇지 않으면 {@code false}
   * @since 1.0
   */
  public static boolean isAtLeastMinYear(int year) {
    return year >= MIN_YEAR.value;
  }

  /**
   * 입력된 연도가 최소 허용 연도 이상인지 검증합니다.
   *
   * <p>정책을 충족하지 않으면 예외를 발생시킵니다.
   *
   * @param year 검증할 연도
   * @throws IllegalArgumentException 입력된 연도가 최소 허용 연도 미만인 경우
   * @since 1.0
   */
  public static void requireAtLeastMinYear(int year) {
    if (!isAtLeastMinYear(year)) {
      throw new IllegalArgumentException(
          "지원되지 않는 연도입니다. 최소 허용 연도: %d, 입력값: %d"
              .formatted(MIN_YEAR.value, year)
      );
    }
  }

  /**
   * 끝 연도를 기준으로 시작 연도를 계산합니다.
   *
   * <p>계산 공식: {@code endYear - RANGE_LENGTH + 1}
   *
   * @param endYear 끝 연도
   * @return 계산된 시작 연도
   * @since 1.0
   */
  public static int calculateStartYear(int endYear) {
    return endYear - RANGE_LENGTH.value + 1;
  }

  /**
   * 시작 연도를 기준으로 끝 연도를 계산합니다.
   *
   * <p>계산 공식: {@code startYear + RANGE_LENGTH - 1}
   *
   * @param startYear 시작 연도
   * @return 계산된 끝 연도
   * @since 1.0
   */
  public static int calculateEndYear(int startYear) {
    return startYear + RANGE_LENGTH.value - 1;
  }
}
