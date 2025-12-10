package com.planitsquare.miniservice.application.service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 연도 범위 생성을 위한 헬퍼 클래스.
 *
 * <p>YearPolicy를 기반으로 연도 검증 및 범위 생성 기능을 제공합니다.
 *
 * @since 1.0
 */
public final class YearRangeHelper {

  private YearRangeHelper() {
    throw new IllegalStateException("Utility class로써 인스턴스화 할 수 없습니다.");
  }

  /**
   * 끝 연도를 기준으로 최근 N년의 연도 목록을 생성합니다.
   *
   * <p>생성되는 연도 개수는 {@link YearPolicy#DEFAULT_RANGE_LENGTH}에 정의된 값을 따릅니다.
   *
   * @param endYear 끝 연도 (가장 최근 연도)
   * @return 시작 연도부터 끝 연도까지의 연도 목록 (오름차순)
   * @throws IllegalArgumentException 끝 연도 또는 계산된 시작 연도가 최소 허용 연도 미만인 경우
   * @since 1.0
   */
  public static List<Integer> generateYearsFromEnd(int endYear, int rangeLength) {
    YearPolicy.requireAtLeastMinYear(endYear);
    int startYear = YearPolicy.calculateStartYear(endYear, rangeLength);
    YearPolicy.requireAtLeastMinYear(startYear);

    return IntStream.rangeClosed(startYear, endYear)
        .boxed()
        .toList();
  }
}
