package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.domain.vo.Country;

/**
 * 비동기 동기화 작업의 결과를 나타내는 Value Object.
 *
 * <p>각 국가-연도 조합에 대한 공휴일 동기화 작업의 성공/실패 정보를 포함합니다.
 * 비동기 작업 완료 후 결과 집계 및 로깅에 사용됩니다.
 *
 * @param country 국가
 * @param year 연도
 * @param success 성공 여부
 * @param syncedCount 동기화된 건수 (성공 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @since 1.0
 */
public record SyncResult(
    Country country,
    int year,
    boolean success,
    int syncedCount,
    String errorMessage
) {

  /**
   * 성공 결과를 생성합니다.
   *
   * @param country 국가
   * @param year 연도
   * @param syncedCount 동기화된 건수
   * @return 성공 결과
   * @since 1.0
   */
  public static SyncResult success(Country country, int year, int syncedCount) {
    return new SyncResult(country, year, true, syncedCount, null);
  }

  /**
   * 실패 결과를 생성합니다.
   *
   * @param country 국가
   * @param year 연도
   * @param errorMessage 에러 메시지
   * @return 실패 결과
   * @since 1.0
   */
  public static SyncResult failure(Country country, int year, String errorMessage) {
    return new SyncResult(country, year, false, 0, errorMessage);
  }

  /**
   * 성공 여부를 반환합니다.
   *
   * @return 성공이면 {@code true}, 실패면 {@code false}
   * @since 1.0
   */
  public boolean isSuccess() {
    return success;
  }
}
