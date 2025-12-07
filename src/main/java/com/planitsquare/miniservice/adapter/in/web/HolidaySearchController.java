package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.response.HolidayResponse;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 공휴일 검색 REST Controller.
 *
 * <p>공휴일 검색 API를 제공합니다.
 *
 * @since 1.0
 */
@Tag(name = "Holiday Search", description = "공휴일 검색 API")
@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
public class HolidaySearchController {

  private final SearchHolidaysUseCase searchHolidaysUseCase;

  /**
   * 공휴일을 검색합니다.
   *
   * @param year        연도 (선택)
   * @param countryCode 국가 코드 (선택)
   * @param from        시작일 (선택)
   * @param to          종료일 (선택)
   * @param type        공휴일 타입 (선택)
   * @param name        공휴일 이름 검색어 (선택)
   * @param pageable    페이징 및 정렬 정보 (기본값: page=0, size=20, sort=date,asc)
   * @return 페이징 처리된 공휴일 목록
   */
  @Operation(summary = "공휴일 검색", description = "다양한 조건으로 공휴일을 검색합니다.")
  @GetMapping
  public ResponseEntity<Page<HolidayResponse>> searchHolidays(
      @Parameter(description = "연도", example = "2024")
      @RequestParam(required = false) Integer year,

      @Parameter(description = "국가 코드", example = "KR")
      @RequestParam(required = false) String countryCode,

      @Parameter(description = "시작일 (yyyy-MM-dd)", example = "2024-01-01")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

      @Parameter(description = "종료일 (yyyy-MM-dd)", example = "2024-12-31")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

      @Parameter(description = "공휴일 타입", example = "PUBLIC")
      @RequestParam(required = false) String type,

      @Parameter(description = "공휴일 이름 검색어", example = "설날")
      @RequestParam(required = false) String name,

      @PageableDefault(size = 20, sort = "date") Pageable pageable
  ) {
    final SearchHolidaysQuery query = SearchHolidaysQuery.builder()
        .year(year)
        .countryCode(countryCode)
        .from(from)
        .to(to)
        .type(type)
        .name(name)
        .pageable(pageable)
        .build();

    return ResponseEntity.ok(searchHolidaysUseCase.search(query).map(HolidayResponse::from));
  }
}
