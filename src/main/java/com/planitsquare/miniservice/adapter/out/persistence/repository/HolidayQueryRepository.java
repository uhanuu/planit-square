package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import org.springframework.data.domain.Page;

/**
 * Holiday 동적 쿼리를 위한 Repository 인터페이스.
 *
 * <p>QueryDSL을 사용한 복잡한 검색 조건을 지원합니다.
 *
 * @since 1.0
 */
public interface HolidayQueryRepository {

  /**
   * 검색 조건에 따라 공휴일을 조회합니다.
   *
   * @param query 검색 조건
   * @return 페이징 처리된 공휴일 JPA Entity 목록
   */
  Page<HolidayJpaEntity> search(SearchHolidaysQuery query);
}
