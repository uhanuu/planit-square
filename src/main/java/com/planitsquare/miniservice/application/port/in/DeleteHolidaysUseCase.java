package com.planitsquare.miniservice.application.port.in;

/**
 * 공휴일 삭제 Use Case.
 *
 * <p>특정 연도와 국가의 공휴일 데이터를 삭제하는 기능을 제공합니다.
 *
 * @since 1.0
 */
public interface DeleteHolidaysUseCase {

  /**
   * 특정 연도와 국가의 공휴일을 삭제합니다.
   *
   * @param command 삭제 커맨드 (연도 및 국가 코드 포함)
   * @return 삭제된 공휴일 건수
   */
  int deleteHolidays(DeleteHolidaysCommand command);
}
