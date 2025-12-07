package com.planitsquare.miniservice.application.port.in;

public interface RefreshHolidaysUseCase {
  RefreshHolidayDto refreshHolidays(RefreshHolidaysCommand command);
}
