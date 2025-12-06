package com.planitsquare.miniservice.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    int page = 0;
    int size = 20;
    String sort = "date,asc";

    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .year(year)
        .countryCode(countryCode)
        .from(from)
        .to(to)
        .type(type)
        .name(name)
        .page(page)
        .size(size)
        .sort(sort)
        .build();

    // Then
    assertThat(query.getYear()).isEqualTo(year);
    assertThat(query.getCountryCode()).isEqualTo(countryCode);
    assertThat(query.getFrom()).isEqualTo(from);
    assertThat(query.getTo()).isEqualTo(to);
    assertThat(query.getType()).isEqualTo(type);
    assertThat(query.getName()).isEqualTo(name);
    assertThat(query.getPage()).isEqualTo(page);
    assertThat(query.getSize()).isEqualTo(size);
    assertThat(query.getSort()).isEqualTo(sort);
  }

  @Test
  @DisplayName("필수 필드만 제공되면 검색 쿼리를 생성한다")
  void 필수_필드만_제공되면_검색_쿼리를_생성한다() {
    // Given
    int page = 0;
    int size = 20;

    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .page(page)
        .size(size)
        .build();

    // Then
    assertThat(query.getPage()).isEqualTo(page);
    assertThat(query.getSize()).isEqualTo(size);
    assertThat(query.getYear()).isNull();
    assertThat(query.getCountryCode()).isNull();
    assertThat(query.getFrom()).isNull();
    assertThat(query.getTo()).isNull();
    assertThat(query.getType()).isNull();
    assertThat(query.getName()).isNull();
  }

  @Test
  @DisplayName("기본값으로 페이지 번호와 크기가 설정된다")
  void 기본값으로_페이지_번호와_크기가_설정된다() {
    // When
    SearchHolidaysQuery query = SearchHolidaysQuery.builder().build();

    // Then
    assertThat(query.getPage()).isEqualTo(0);
    assertThat(query.getSize()).isEqualTo(20);
  }
}
