package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;

import java.util.List;

public interface FetchHolidaysPort {
  List<Holiday> fetchHolidays(int year, Country country);
}
