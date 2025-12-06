package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.annotation.RecordSyncHistory;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.application.util.JobIdContext;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
   * Job ID는 {@link JobIdContext}에서 자동으로 가져옵니다.
   *
   * @param country 국가
   * @param year 연도
   * @return 저장된 공휴일 목록
   * @since 1.0
   */
  @RecordSyncHistory(
      country = "#country",
      year = "#year"
  )
  public List<Holiday> syncHolidaysForCountryAndYear(Country country, int year) {
    log.debug("공휴일 조회 시작 - 국가: {}, 연도: {}", country.getCode(), year);

    List<Holiday> holidays = fetchHolidaysPort.fetchHolidays(year, country);
    saveAllHolidaysPort.saveAllHolidays(holidays);

    log.debug("공휴일 저장 완료 - 국가: {}, 연도: {}, 건수: {}",
        country.getCode(), year, holidays.size());

    return holidays;
  }
}
