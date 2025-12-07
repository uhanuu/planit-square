package com.planitsquare.miniservice.application.port.in;

import com.planitsquare.miniservice.domain.model.Holiday;
import org.springframework.data.domain.Page;

/**
 * 공휴일 검색 Use Case.
 *
 * <p>다양한 조건으로 공휴일을 검색하고 페이징 처리된 결과를 반환합니다.
 *
 * @since 1.0
 */
public interface SearchHolidaysUseCase {

  /**
   * 검색 조건에 따라 공휴일을 조회합니다.
   *
   * @param query 검색 조건
   * @return 페이징 처리된 공휴일 목록
   */
  Page<Holiday> search(SearchHolidaysQuery query);
}
