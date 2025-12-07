package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.domain.model.Holiday;
import org.springframework.data.domain.Page;

/**
 * 공휴일 검색을 위한 출력 포트.
 *
 * <p>데이터 저장소에서 공휴일을 검색하는 기능을 제공합니다.
 *
 * @since 1.0
 */
public interface SearchHolidaysPort {

  /**
   * 검색 조건에 따라 공휴일을 조회합니다.
   *
   * @param query 검색 조건
   * @return 페이징 처리된 공휴일 목록
   */
  Page<Holiday> searchHolidays(SearchHolidaysQuery query);
}
