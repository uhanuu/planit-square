package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.port.in.DeleteHolidaysCommand;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.common.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공휴일 삭제 Application Service.
 *
 * <p>특정 연도와 국가의 공휴일을 삭제하는 기능을 제공합니다.
 *
 * @since 1.0
 */
@UseCase
@RequiredArgsConstructor
@Slf4j
public class HolidayDeletionService implements DeleteHolidaysUseCase {

  private final DeleteHolidaysPort deleteHolidaysPort;

  /**
   * 특정 연도와 국가의 공휴일을 삭제합니다.
   *
   * @param command 삭제 커맨드 (연도 및 국가 코드 포함)
   * @return 삭제된 공휴일 건수
   */
  @Override
  public int deleteHolidays(DeleteHolidaysCommand command) {
    log.info("공휴일 삭제 시작 - 연도: {}, 국가 코드: {}",
        command.year(), command.countryCode().code());

    int deletedCount = deleteHolidaysPort.deleteByYearAndCountryCode(
        command.year(),
        command.countryCode()
    );

    log.info("공휴일 삭제 완료 - 연도: {}, 국가 코드: {}, 삭제 건수: {}",
        command.year(), command.countryCode().code(), deletedCount);

    return deletedCount;
  }
}
