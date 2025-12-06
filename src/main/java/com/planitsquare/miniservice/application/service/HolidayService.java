package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.annotation.SyncJob;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.FetchCountriesPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllCountriesPort;
import com.planitsquare.miniservice.common.UseCase;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 공휴일 관리 Application Service.
 *
 * <p>공휴일 데이터의 업로드, 조회, 관리 기능을 제공하는 Use Case 구현체입니다.
 * 외부 API를 통해 국가별 공휴일 정보를 가져와 저장합니다.
 *
 * @since 1.0
 */
@UseCase
@RequiredArgsConstructor
@Slf4j
public class HolidayService implements UploadHolidaysUseCase {
  private final FindCountryPort findCountryPort;
  private final FetchCountriesPort fetchCountriesPort;
  private final SaveAllCountriesPort saveAllCountriesPort;
  private final HolidaySyncService holidaySyncService;

  /**
   * 지정된 연도 범위의 공휴일 데이터를 업로드합니다.
   *
   * <p>실행 타입에 따라 국가 목록을 확보하고, 각 국가와 연도별로 공휴일을 조회하여 저장합니다.
   * 비동기로 실행되며, 각 실행은 독립적인 트랜잭션 내에서 수행됩니다.
   *
   * <p>Job 시작/완료는 {@link SyncJob} 어노테이션을 통해 AOP가 자동으로 처리합니다.
   *
   * @param command 업로드 커맨드 (연도 및 실행 타입 포함)
   * @since 1.0
   */
  @Override
  @SyncJob(executionType = "#command.executionType()")
  public void uploadHolidays(UploadHolidayCommand command) {
    final SyncExecutionType syncExecutionType = command.executionType();

    log.info("공휴일 업로드 시작 - 연도: {}, 실행 타입: {}",
        command.year(), syncExecutionType.getDisplayName());

    List<Integer> years = YearRangeHelper.generateYearsFromEnd(command.year());
    List<Country> countries = ensureCountriesLoaded(syncExecutionType);

    log.info("공휴일 업로드 진행 - 국가 수: {}, 처리 연도들: {}", countries.size(), years);
    fetchAndSaveHolidaysForAllCountriesAndYears(countries, years);

    log.info("공휴일 업로드 완료 - 총 {}개 국가, {}개 연도 처리", countries.size(), years.size());
  }

  /**
   * 모든 국가와 연도에 대해 공휴일을 조회하고 저장합니다.
   *
   * <p>각 국가-연도 조합에 대해 외부 API로부터 공휴일을 조회하여 데이터베이스에 저장합니다.
   * 동기화는 {@link HolidaySyncService}를 통해 수행되며, AOP를 통한 이력 기록이 자동으로 처리됩니다.
   *
   * @param countries 국가 목록
   * @param years 연도 목록
   * @since 1.0
   */
  private void fetchAndSaveHolidaysForAllCountriesAndYears(
      List<Country> countries,
      List<Integer> years
  ) {
    countries.forEach(country ->
        years.forEach(year ->
            holidaySyncService.syncHolidaysForCountryAndYear(country, year)
        )
    );
  }

  /**
   * 국가 목록을 확보합니다.
   *
   * <p>시스템 최초 적재(INITIAL_SYSTEM_LOAD)인 경우, 외부 API로부터 국가 목록을 조회하여
   * 데이터베이스에 저장합니다. 그 외의 경우에는 데이터베이스에서 기존 국가 목록을 조회합니다.
   *
   * @param executionType 실행 타입
   * @return 국가 목록
   * @since 1.0
   */
  private List<Country> ensureCountriesLoaded(SyncExecutionType executionType) {
    if (executionType.isInitialSystemLoad()) {
      log.info("최초 시스템 적재 - 외부 API에서 국가 목록 조회");
      List<Country> countries = fetchCountriesPort.fetchCountries();
      saveAllCountriesPort.saveAllCountries(countries);
      log.info("국가 목록 저장 완료 - 건수: {}", countries.size());
      return countries;
    }

    log.debug("데이터베이스에서 국가 목록 조회");
    return findCountryPort.findAll();
  }
}
