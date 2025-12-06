package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.*;
import com.planitsquare.miniservice.common.UseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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
  private final FetchHolidaysPort fetchHolidaysPort;
  private final FetchCountriesPort fetchCountriesPort;
  private final SaveAllCountriesPort saveAllCountriesPort;
  private final SaveAllHolidaysPort saveAllHolidaysPort;
  private final RecordSyncHistoryPort recordSyncHistoryPort;
  private final SyncJobPort syncJobPort;

  /**
   * 지정된 연도 범위의 공휴일 데이터를 업로드합니다.
   *
   * <p>실행 타입에 따라 국가 목록을 확보하고, 각 국가와 연도별로 공휴일을 조회하여 저장합니다.
   * 모든 작업은 단일 트랜잭션 내에서 수행됩니다.
   *
   * @param command 업로드 커맨드 (연도 및 실행 타입 포함)
   * @since 1.0
   */
  @Override
  public void uploadHolidays(UploadHolidayCommand command) {

    final SyncExecutionType syncExecutionType = command.executionType();
    final Long jobId = syncJobPort.startJob(syncExecutionType);

    log.info("공휴일 업로드 시작 - 연도: {}, 실행 타입: {}",
        command.year(), syncExecutionType.getDisplayName());

    List<Integer> years = YearRangeHelper.generateYearsFromEnd(command.year());
    List<Country> countries = ensureCountriesLoaded(syncExecutionType);

    log.info("공휴일 업로드 진행 - 국가 수: {}, 처리 연도들: {}", countries.size(), years);

    fetchAndSaveHolidaysForAllCountriesAndYears(countries, years, jobId);
    log.info("공휴일 업로드 완료 - 총 {}개 국가, {}개 연도 처리", countries.size(), years.size());
    syncJobPort.completeJob(jobId);
  }

  /**
   * 모든 국가와 연도에 대해 공휴일을 조회하고 저장합니다.
   *
   * <p>각 국가-연도 조합에 대해 외부 API로부터 공휴일을 조회하여 데이터베이스에 저장합니다.
   *
   * @param countries 국가 목록
   * @param years 연도 목록
   * @param jobId jobId
   * @since 1.0
   */
  private void fetchAndSaveHolidaysForAllCountriesAndYears(
      List<Country> countries,
      List<Integer> years,
      Long jobId
  ) {
    countries.forEach(country ->
        years.forEach(year ->
            fetchAndSaveHolidaysForCountryAndYear(country, year, jobId)
        )
    );
  }

  /**
   * 특정 국가와 연도에 대해 공휴일을 조회하고 저장합니다.
   *
   * <p>동기화 작업의 성공/실패 이력을 SyncHistory에 기록합니다.
   *
   * @param country 국가
   * @param year 연도
   * @since 1.0
   */
  private void fetchAndSaveHolidaysForCountryAndYear(
      Country country,
      int year,
      Long jobId
  ) {
    long startTime = System.currentTimeMillis();
    log.debug("공휴일 조회 시작 - 국가: {}, 연도: {}", country.getCode(), year);


    try {
      List<Holiday> holidays = fetchHolidaysPort.fetchHolidays(year, country);
      saveAllHolidaysPort.saveAllHolidays(holidays);

      long durationMillis = System.currentTimeMillis() - startTime;
      recordSyncHistoryPort.recordSuccess(
          jobId,
          country,
          year,
          holidays.size(),
          durationMillis,
          LocalDateTime.now()
      );

      log.debug("공휴일 저장 완료 - 국가: {}, 연도: {}, 건수: {}",
          country.getCode(), year, holidays.size());

    } catch (Exception e) {
      long durationMillis = System.currentTimeMillis() - startTime;
      recordSyncHistoryPort.recordFailure(
          jobId,
          country,
          year,
          e.getMessage(),
          durationMillis,
          LocalDateTime.now()
      );

      log.error("공휴일 조회/저장 실패 - 국가: {}, 연도: {}, 에러: {}",
          country.getCode(), year, e.getMessage(), e);

      throw e; // 예외를 다시 던져서 트랜잭션 롤백 유도
    }
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
