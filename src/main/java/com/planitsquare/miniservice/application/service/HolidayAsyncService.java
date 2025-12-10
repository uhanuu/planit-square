package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.annotation.SyncJob;
import com.planitsquare.miniservice.application.port.in.SyncHolidayDataUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FetchCountriesPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllCountriesPort;
import com.planitsquare.miniservice.application.util.JobIdContext;
import com.planitsquare.miniservice.common.UseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * 공휴일 병렬 업로드 Application Service.
 *
 * <p>여러 국가와 연도의 공휴일을 병렬로 조회하여 저장하는 오케스트레이션을 담당합니다.
 * CompletableFuture와 ThreadPoolExecutor를 사용하여 병렬 처리를 수행합니다.
 *
 * @since 1.0
 */
@UseCase
@RequiredArgsConstructor
@Slf4j
public class HolidayAsyncService implements UploadHolidaysUseCase, SyncHolidayDataUseCase {

  private final FindCountryPort findCountryPort;
  private final FetchCountriesPort fetchCountriesPort;
  private final SaveAllCountriesPort saveAllCountriesPort;
  private final HolidaySyncInnerService holidaySyncInnerService;
  private final Executor holidayTaskExecutor;
  private final DeleteHolidaysPort deleteHolidaysPort;

  /**
   * 지정된 연도 범위의 공휴일 데이터를 병렬로 업로드합니다.
   *
   * <p>실행 타입에 따라 국가 목록을 확보하고, 각 국가와 연도별로 공휴일을 조회하여 저장합니다.
   * 병렬로 실행되며, 각 실행은 독립적인 트랜잭션 내에서 수행됩니다.
   *
   * <p>Job 시작/완료는 {@link SyncJob} 어노테이션을 통해 AOP가 자동으로 처리합니다.
   *
   * @param command 업로드 커맨드 (연도 및 실행 타입 포함)
   * @return 동기화 결과 목록
   * @since 1.0
   */
  @Override
  @SyncJob(executionType = "#command.executionType()")
  public List<SyncResult> uploadHolidays(UploadHolidayCommand command) {
    YearPolicy.requireAtLeastMinYear(command.year());
    final SyncExecutionType syncExecutionType = command.executionType();

    log.info("공휴일 업로드 시작 (병렬처리) - 연도: {}, 실행 타입: {}",
        command.year(), syncExecutionType.getDisplayName());

    List<Integer> years = YearRangeHelper.generateYearsFromEnd(command.year(), command.yearRangeLength());
    List<Country> countries = ensureCountriesLoaded(syncExecutionType);

    log.info("공휴일 병렬 업로드 진행 - 국가 수: {}, 처리 연도들: {}", countries.size(), years);
    List<SyncResult> results = fetchAndSaveHolidaysForAllCountriesAndYearsAsync(countries, years);

    log.info("공휴일 업로드 완료 - 총 {}개 국가, {}개 연도 처리", countries.size(), years.size());
    return results;
  }

  /**
   * 모든 국가와 연도에 대해 병렬로 공휴일을 조회하고 저장합니다.
   *
   * <p>각 국가-연도 조합은 독립적인 CompletableFuture 태스크로 실행됩니다.
   * 개별 태스크 실패는 전체 작업을 중단시키지 않으며, 모든 태스크 완료까지 대기합니다.
   *
   * @param countries 국가 목록
   * @param years 연도 목록
   * @return 동기화 결과 목록
   * @since 1.0
   */
  private List<SyncResult> fetchAndSaveHolidaysForAllCountriesAndYearsAsync(
      List<Country> countries,
      List<Integer> years
  ) {
    Long jobId = JobIdContext.getJobId();

    // 1. 병렬 작업 생성 및 실행
    List<CompletableFuture<SyncResult>> futures = createAsyncTasks(jobId, countries, years);

    // 2. 모든 작업 완료 대기 및 결과 수집
    List<SyncResult> results = waitForAllTasksAndCollectResults(futures);

    log.info("병렬 동기화 완료 - {}", SyncStats.from(results).toLogString());
    return results;
  }

  /**
   * 모든 국가-연도 조합에 대한 병렬 작업을 생성합니다.
   *
   * @param jobId Job ID
   * @param countries 국가 목록
   * @param years 연도 목록
   * @return CompletableFuture 리스트
   * @since 1.0
   */
  private List<CompletableFuture<SyncResult>> createAsyncTasks(
      Long jobId,
      List<Country> countries,
      List<Integer> years
  ) {
    return countries.stream()
        .flatMap(country -> years.stream()
            .map(year -> createSyncTask(jobId, country, year))
        )
        .toList();
  }

  /**
   * 모든 병렬 작업이 완료될 때까지 대기하고 결과를 수집합니다.
   *
   * <p>개별 작업 실패는 {@link SyncResult#failure}로 처리되어,
   * 전체 작업 완료에 영향을 주지 않습니다.
   *
   * @param futures CompletableFuture 리스트
   * @return 수집된 결과 리스트
   * @since 1.0
   */
  private List<SyncResult> waitForAllTasksAndCollectResults(
      List<CompletableFuture<SyncResult>> futures
  ) {
    CompletableFuture<Void> allTasks = CompletableFuture.allOf(
        futures.toArray(new CompletableFuture[0])
    );

    try {
      allTasks.join(); // 모든 작업 완료까지 대기
    } catch (CompletionException e) {
      // 개별 작업 실패는 SyncResult.failure로 처리되므로 무시
      log.debug("일부 병렬 작업 실패 (개별 결과에 기록됨)", e);
    }

    // 모든 결과 수집 (성공/실패 모두 포함)
    return futures.stream()
        .map(CompletableFuture::join)
        .toList();
  }

  /**
   * 단일 국가-연도 조합에 대한 동기화 태스크를 생성합니다.
   *
   * <p>CompletableFuture를 사용하여 병렬로 실행되며,
   * 개별 실패는 {@link SyncResult#failure}로 기록됩니다.
   *
   * @param jobId Job ID
   * @param country 국가
   * @param year 연도
   * @return CompletableFuture 태스크
   * @since 1.0
   */
  private CompletableFuture<SyncResult> createSyncTask(Long jobId, Country country, int year) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        SyncHolidayCommand syncCommand = new SyncHolidayCommand(jobId, country, year);
        List<Holiday> holidays = holidaySyncInnerService.syncHolidaysForCountryAndYear(syncCommand);
        return SyncResult.success(country, year, holidays.size());
      } catch (Exception e) {
        log.error("동기화 실패 - 국가: {}, 연도: {}, 에러: {}",
            country.getCode(), year, e.getMessage(), e);
        return SyncResult.failure(country, year, e.getMessage());
      }
    }, holidayTaskExecutor);
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
    if (!executionType.isInitialSystemLoad()) {
      log.debug("데이터베이스에서 국가 목록 조회");
      return findCountryPort.findAll();
    }

    log.info("최초 시스템 적재 - 외부 API에서 국가 목록 조회");
    List<Country> countries = fetchCountriesPort.fetchCountries();
    saveAllCountriesPort.saveAllCountries(countries);
    log.info("국가 목록 저장 완료 - 건수: {}", countries.size());
    return countries;
  }

  /**
   * 지정된 연도 범위의 공휴일 데이터를 병렬로 업로드합니다.
   *
   * 외부 API로 호출된 각 국가와 연도별로 공휴일을 전달 받아 저장합니다.
   * 병렬로 실행되며, 각 실행은 독립적인 트랜잭션 내에서 수행됩니다.
   *
   * <p>Job 시작/완료는 {@link SyncJob} 어노테이션을 통해 AOP가 자동으로 처리합니다.
   *
   * @param command 업로드 커맨드 (연도 및 실행 타입과 외부 API 결과가 전달됩니다.)
   * @return 동기화 결과 목록
   * @since 1.0
   */
  @Override
  @SyncJob(executionType = "#command.executionType()")
  public List<SyncResult> syncAnnualHolidays(UploadHolidayCommand command) {
    YearPolicy.requireAtLeastMinYear(command.year());
    final SyncExecutionType syncExecutionType = command.executionType();
    List<Integer> years = YearRangeHelper.generateYearsFromEnd(command.year(), command.yearRangeLength());

    log.info("연간 공휴일 동기화 시작 - 연도 목록: {}, 실행 타입: {}",
        years, syncExecutionType.getDisplayName());

    int deletedCount = deleteHolidaysPort.deleteByYear(years);
    log.info("기존 공휴일 데이터 삭제 완료 - 삭제 건수: {}", deletedCount);

    List<Country> countries = ensureCountriesLoaded(syncExecutionType);

    log.info("공휴일 병렬 업로드 진행 - 국가 수: {}, 처리 연도들: {}", countries.size(), years);
    List<SyncResult> results = fetchAndSaveHolidaysForAllCountriesAndYearsAsync(countries, years);

    log.info("연간 공휴일 동기화 완료 - 총 {}개 국가, {}개 연도 처리", countries.size(), years.size());
    return results;
  }
}
