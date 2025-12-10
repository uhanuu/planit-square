package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.exception.CountryNotFoundException;
import com.planitsquare.miniservice.application.port.in.*;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.common.UseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 공휴일 관리 Application Service.
 *
 * <p>공휴일 데이터의 삭제 및 리프레시 기능을 제공하는 Use Case 구현체입니다.
 * 트랜잭션 범위를 최소화하여 외부 API 호출 시간을 트랜잭션에 포함하지 않습니다.
 *
 * @since 1.0
 */
@UseCase
@RequiredArgsConstructor
@Slf4j
public class HolidayManagementService implements DeleteHolidaysUseCase, RefreshHolidaysUseCase {

  private final FindCountryPort findCountryPort;
  private final DeleteHolidaysPort deleteHolidaysPort;
  private final SaveAllHolidaysPort saveAllHolidaysPort;
  private final SyncJobValidator syncJobValidator;

  /**
   * 특정 연도와 국가의 공휴일을 삭제합니다.
   *
   * <p>삭제 작업 전에 현재 실행 중인 Job이 있는지 검증하여 데이터 무결성을 보장합니다.
   *
   * @param command 삭제 커맨드 (연도 및 국가 코드 포함)
   * @return 삭제된 공휴일 건수
   * @since 1.0
   */
  @Override
  public int deleteHolidays(DeleteHolidaysCommand command) {
    final int year = command.year();
    final CountryCode countryCode = command.countryCode();
    log.info("공휴일 삭제 시작 - 연도: {}, 국가 코드: {}", year, countryCode.code());

    syncJobValidator.validateNoRunningJob();
    YearPolicy.requireAtLeastMinYear(year);
    validateCountryExists(countryCode);

    int deletedCount = deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode);
    log.info("공휴일 삭제 완료 - 연도: {}, 국가 코드: {}, 삭제 건수: {}",
        year, countryCode.code(), deletedCount);

    return deletedCount;
  }

  /**
   * 특정 연도와 국가의 공휴일을 리프레시합니다.
   *
   * <p>기존 데이터를 삭제하고 새 데이터를 저장합니다.
   * 트랜잭션은 DB 작업(삭제, 저장)에만 적용하여 외부 API 호출 시간을 트랜잭션에 포함하지 않습니다.
   *
   * @param command 리프레시 커맨드 (연도 및 국가 코드 포함)
   * @return 삭제 및 삽입 건수를 포함한 결과
   * @since 1.0
   */
  @Override
  @Transactional
  public RefreshHolidayDto refreshHolidays(RefreshHolidaysCommand command) {
    final int year = command.year();
    final CountryCode countryCode = command.countryCode();
    final List<Holiday> holidays = command.holidays();
    log.info("공휴일 리프레시 시작 - 연도: {}, 국가 코드: {}", year, countryCode.code());

    syncJobValidator.validateNoRunningJob();
    YearPolicy.requireAtLeastMinYear(year);

    final int deletedCount = deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode);
    saveAllHolidaysPort.saveAllHolidays(holidays);

    log.info("공휴일 리프레시 완료 - 연도: {}, 국가 코드: {}, 삭제: {}, 삽입: {}",
        year, countryCode.code(), deletedCount, holidays.size());

    return new RefreshHolidayDto(deletedCount, holidays.size());
  }

  /**
   * 국가 코드의 존재 여부를 검증합니다.
   *
   * @param countryCode 검증할 국가 코드
   * @throws CountryNotFoundException 국가 코드가 존재하지 않는 경우
   * @since 1.0
   */
  private void validateCountryExists(CountryCode countryCode) {
    if (!findCountryPort.existsByCode(countryCode.code())) {
      throw new CountryNotFoundException(countryCode.code() + "의 해당하는 국가 코드가 존재하지 않습니다.");
    }
  }
}
