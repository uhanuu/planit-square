package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysUseCase;
import com.planitsquare.miniservice.application.port.out.SearchHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * HolidaySearchService 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("공휴일 검색 서비스 테스트")
class HolidaySearchServiceTest {

  @Mock
  private SearchHolidaysPort searchHolidaysPort;

  private SearchHolidaysUseCase searchHolidaysUseCase;

  @BeforeEach
  void setUp() {
    searchHolidaysUseCase = new HolidaySearchService(searchHolidaysPort);
  }

  @Test
  @DisplayName("검색 조건에 따라 공휴일을 조회한다")
  void 검색_조건에_따라_공휴일을_조회한다() {
    // Given
    PageRequest page = PageRequest.of(0, 20);
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .year(2024)
        .countryCode("KR")
        .pageable(page)
        .build();

    Holiday holiday = createHoliday(1L, "설날");
    Page<Holiday> expectedPage = new PageImpl<>(
        List.of(holiday),
        page,
        1
    );

    given(searchHolidaysPort.searchHolidays(any(SearchHolidaysQuery.class)))
        .willReturn(expectedPage);

    // When
    Page<Holiday> result = searchHolidaysUseCase.search(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(20);
    then(searchHolidaysPort).should().searchHolidays(query);
  }

  @Test
  @DisplayName("빈 검색 결과를 반환할 수 있다")
  void 빈_검색_결과를_반환할_수_있다() {
    // Given
    SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .year(2099)
        .countryCode("ZZ")
        .build();

    Page<Holiday> emptyPage = new PageImpl<>(
        List.of(),
        PageRequest.of(0, 20),
        0
    );

    given(searchHolidaysPort.searchHolidays(any(SearchHolidaysQuery.class)))
        .willReturn(emptyPage);

    // When
    Page<Holiday> result = searchHolidaysUseCase.search(query);

    // Then
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
    then(searchHolidaysPort).should().searchHolidays(query);
  }

  private Holiday createHoliday(Long id, String name) {
    return new Holiday(
        new HolidayId(id),
        Country.of("KR", "대한민국"),
        name,
        name,
        LocalDate.of(2024, 1, 1),
        new HolidayMetadata(
            true,
            true,
            2020,
            List.of("Public"),
            List.of()
        )
    );
  }
}
