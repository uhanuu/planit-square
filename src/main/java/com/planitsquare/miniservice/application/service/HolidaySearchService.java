package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.SearchHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공휴일 검색 Application Service.
 *
 * <p>공휴일 검색 Use Case를 구현합니다.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidaySearchService implements SearchHolidaysUseCase {

  private final SearchHolidaysPort searchHolidaysPort;

  @Override
  public Page<Holiday> search(SearchHolidaysQuery query) {
    return searchHolidaysPort.searchHolidays(query);
  }
}
