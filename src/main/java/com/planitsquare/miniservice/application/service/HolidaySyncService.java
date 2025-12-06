package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.annotation.RecordSyncHistory;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 공휴일 동기화를 담당하는 Application Service.
 *
 * <p>특정 국가와 연도에 대해 공휴일을 조회하고 저장하는 책임을 가집니다.
 * AOP를 통한 동기화 이력 기록을 위해 별도 서비스로 분리되었습니다.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncService {

  private final FetchHolidaysPort fetchHolidaysPort;
  private final SaveAllHolidaysPort saveAllHolidaysPort;

  /**
   * 특정 국가와 연도에 대해 공휴일을 조회하고 저장합니다.
   *
   * <p>동기화 작업의 성공/실패 이력은 {@link RecordSyncHistory} 어노테이션을 통해 AOP가 자동으로 기록합니다.
   *
   * @param command 동기화 커맨드 (Job ID, 국가, 연도 포함)
   * @return 저장된 공휴일 목록
   * @since 1.0
   */
  @RecordSyncHistory(
      jobId = "#command.jobId()",
      country = "#command.country()",
      year = "#command.year()"
  )
  public List<Holiday> syncHolidaysForCountryAndYear(SyncHolidayCommand command) {
    log.debug("공휴일 조회 시작 - 국가: {}, 연도: {}",
        command.country().getCode(), command.year());

    List<Holiday> holidays = fetchHolidaysPort.fetchHolidays(command.year(), command.country());
    saveAllHolidaysPort.saveAllHolidays(holidays);

    log.debug("공휴일 저장 완료 - 국가: {}, 연도: {}, 건수: {}",
        command.country().getCode(), command.year(), holidays.size());

    return holidays;
  }
}
