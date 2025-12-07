package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.vo.CountryCode;

/**
 * 공휴일 삭제 Port.
 *
 * <p>특정 연도와 국가의 공휴일을 삭제하는 기능을 제공합니다.
 *
 * @since 1.0
 */
public interface DeleteHolidaysPort {

  /**
   * 특정 연도와 국가의 모든 공휴일을 삭제합니다.
   *
   * @param year 연도
   * @param countryCode 국가 코드
   * @return 삭제된 공휴일 건수
   */
  int deleteByYearAndCountryCode(int year, CountryCode countryCode);
}
