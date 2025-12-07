package com.planitsquare.miniservice.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SearchHolidaysQuery 테스트.
 */
@DisplayName("공휴일 검색 쿼리 테스트")
class SearchHolidaysQueryTest {

  @Test
  @DisplayName("모든 필드가 제공되면 검색 쿼리를 생성한다")
  void 모든_필드가_제공되면_검색_쿼리를_생성한다() {
    // Given
    Integer year = 2024;
    String countryCode = "KR";
    LocalDate from = LocalDate.of(2024, 1, 1);
    LocalDate to = LocalDate.of(2024, 12, 31);
    String type = "PUBLIC";
    String name = "설날";
    Pageable pageable = PageRequest.of(0, 20, Sort.by("date").ascending());

    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .year(year)
        .countryCode(countryCode)
        .from(from)
        .to(to)
        .type(type)
        .name(name)
        .pageable(pageable)
        .build();

    // Then
    assertThat(query.getYear()).isEqualTo(year);
    assertThat(query.getCountryCode()).isEqualTo(countryCode);
    assertThat(query.getFrom()).isEqualTo(from);
    assertThat(query.getTo()).isEqualTo(to);
    assertThat(query.getType()).isEqualTo(type);
    assertThat(query.getName()).isEqualTo(name);
    assertThat(query.getPageable()).isEqualTo(pageable);
    assertThat(query.getPageable().getPageNumber()).isEqualTo(0);
    assertThat(query.getPageable().getPageSize()).isEqualTo(20);
  }

  @Test
  @DisplayName("필수 필드만 제공되면 검색 쿼리를 생성한다")
  void 필수_필드만_제공되면_검색_쿼리를_생성한다() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);

    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .pageable(pageable)
        .build();

    // Then
    assertThat(query.getPageable()).isEqualTo(pageable);
    assertThat(query.getYear()).isNull();
    assertThat(query.getCountryCode()).isNull();
    assertThat(query.getFrom()).isNull();
    assertThat(query.getTo()).isNull();
    assertThat(query.getType()).isNull();
    assertThat(query.getName()).isNull();
  }

  @Test
  @DisplayName("Pageable이 올바르게 설정된다")
  void Pageable이_올바르게_설정된다() {
    // Given
    Pageable pageable = PageRequest.of(1, 50, Sort.by("name").descending());

    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .pageable(pageable)
        .build();

    // Then
    assertThat(query.getPageable().getPageNumber()).isEqualTo(1);
    assertThat(query.getPageable().getPageSize()).isEqualTo(50);
    assertThat(query.getPageable().getSort().isSorted()).isTrue();
  }
}
