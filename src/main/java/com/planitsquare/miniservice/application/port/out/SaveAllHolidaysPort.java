package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.model.Holiday;

import java.util.List;

public interface SaveAllHolidaysPort {
  void saveAllHolidays(List<Holiday> holidays);
}
