package com.planitsquare.miniservice.adapter.out.persistence;

import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.mapper.HolidayMapper;
import com.planitsquare.miniservice.adapter.out.persistence.repository.HolidayQueryRepository;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.application.port.out.SearchHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * 공휴일 검색 Adapter.
 *
 * <p>QueryDSL Repository를 사용하여 공휴일을 검색하고
 * JPA Entity를 Domain 객체로 변환합니다.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class HolidaySearchAdapter implements SearchHolidaysPort {

  private final HolidayQueryRepository holidayQueryRepository;
  private final HolidayMapper holidayMapper;

  @Override
  public Page<Holiday> searchHolidays(SearchHolidaysQuery query) {
    Page<HolidayJpaEntity> entityPage = holidayQueryRepository.search(query);
    return entityPage.map(holidayMapper::toDomain);
  }
}
