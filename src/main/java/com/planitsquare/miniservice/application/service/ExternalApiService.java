package com.planitsquare.miniservice.application.service;


import com.planitsquare.miniservice.application.exception.CountryNotFoundException;
import com.planitsquare.miniservice.application.port.in.FetchHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.common.UseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UseCase
@Slf4j
@RequiredArgsConstructor
public class ExternalApiService implements FetchHolidaysUseCase {
  private final FetchHolidaysPort fetchHolidaysPort;
  private final FindCountryPort findCountryPort;

  @Override
  public List<Holiday> fetchHolidays(int year, CountryCode countryCode) {
    final String code = countryCode.code();
    final Country country = findCountryPort.findByCode(code)
        .orElseThrow(() -> new CountryNotFoundException(code + "의 해당하는 국가가 존재하지 않습니다."));

    log.debug("외부 API 호출 시작 - 국가: {}, 연도: {}", country.getCode(), year);
    List<Holiday> holidays = fetchHolidaysPort.fetchHolidays(year, country);
    log.debug("외부 API 호출 완료 - 조회 건수: {}", holidays.size());

    return holidays;
  }
}
