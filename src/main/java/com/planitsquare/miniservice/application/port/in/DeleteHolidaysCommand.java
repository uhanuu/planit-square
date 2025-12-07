package com.planitsquare.miniservice.application.port.in;

import com.planitsquare.miniservice.domain.vo.CountryCode;

/**
 * 공휴일 삭제 커맨드.
 *
 * <p>특정 연도와 국가의 공휴일을 삭제하기 위한 커맨드 객체입니다.
 *
 * @param year 삭제할 연도
 * @param countryCode 삭제할 국가 코드
 * @since 1.0
 */
public record DeleteHolidaysCommand(
    Integer year,
    CountryCode countryCode
) {
  public DeleteHolidaysCommand {
    if (year == null) {
      throw new IllegalArgumentException("연도가 존재하지 않습니다.");
    }
    if (countryCode == null) {
      throw new IllegalArgumentException("국가 코드가 존재하지 않습니다.");
    }
  }
}
