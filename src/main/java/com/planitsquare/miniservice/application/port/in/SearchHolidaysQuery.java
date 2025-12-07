package com.planitsquare.miniservice.application.port.in;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * 공휴일 검색 쿼리.
 *
 * <p>공휴일 검색을 위한 다양한 필터 조건을 캡슐화합니다.
 * 모든 필터는 선택적입니다.
 *
 * @since 1.0
 */
@Getter
@Builder
public class SearchHolidaysQuery {

  /**
   * 연도 필터.
   */
  private final Integer year;

  /**
   * 국가 코드 필터 (예: KR, US, JP).
   */
  private final String countryCode;

  /**
   * 시작일 필터 (기간 검색 시작).
   */
  private final LocalDate from;

  /**
   * 종료일 필터 (기간 검색 종료).
   */
  private final LocalDate to;

  /**
   * 공휴일 타입 필터 (예: PUBLIC, BANK, OPTIONAL).
   */
  private final String type;

  /**
   * 공휴일 이름 검색어 (부분 일치).
   */
  private final String name;

  /**
   * 페이징 및 정렬 정보.
   */
  private final Pageable pageable;
}
