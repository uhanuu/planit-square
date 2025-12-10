package com.planitsquare.miniservice.application.port.in;

import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.CountryCode;

import java.util.List;

public interface FetchHolidaysUseCase {
  List<Holiday> fetchHolidays(int year, CountryCode countryCode);
}
