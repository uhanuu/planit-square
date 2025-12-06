package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.*;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService implements UploadHolidaysUseCase {
  private final FindCountryPort findCountryPort;
  private final FetchHolidaysPort fetchHolidaysPort;
  private final FetchCountriesPort fetchCountriesPort;
  private final SaveAllCountriesPort saveAllCountriesPort;
  private final SaveAllHolidaysPort saveAllHolidaysPort;

  @Override
  public void uploadHolidays(UploadHolidayCommand command) {
    final int year = command.year();
    final SyncExecutionType executionType = command.executionType();
    final List<Country> countries = ensureCountriesLoaded(executionType);

    countries.forEach(country -> {
      List<Holiday> holidays = fetchHolidaysPort.fetchHolidays(year, country);
      saveAllHolidaysPort.saveAllHolidays(holidays);
    });

  }

  /**
   * 국가 목록을 확보합니다.
   *
   * <p>시스템 최초 적재(INITIAL_SYSTEM_LOAD)인 경우, 외부 API로부터 국가 목록을 조회하여
   * 데이터베이스에 저장합니다. 그 외의 경우에는 데이터베이스에서 기존 국가 목록을 조회합니다.
   *
   * @param executionType 실행 타입
   * @return 국가 목록
   */
  private List<Country> ensureCountriesLoaded(SyncExecutionType executionType) {
    if (executionType.isInitialSystemLoad()) {
      List<Country> countries = fetchCountriesPort.fetchCountries();
      saveAllCountriesPort.saveAllCountries(countries);
      return countries;
    }

    return findCountryPort.findAll();
  }
}
