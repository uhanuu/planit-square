package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.HolidayResponseDto;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysUseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
   * @param page        페이지 번호 (기본값: 0)
   * @param size        페이지 크기 (기본값: 20)
   * @param sort        정렬 조건 (기본값: date,asc)
   * @return 페이징 처리된 공휴일 목록
   */
  @Operation(summary = "공휴일 검색", description = "다양한 조건으로 공휴일을 검색합니다.")
  @GetMapping
  public ResponseEntity<Page<HolidayResponseDto>> searchHolidays(
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

      @Parameter(description = "페이지 번호 (0-based)", example = "0")
      @RequestParam(defaultValue = "0") int page,

      @Parameter(description = "페이지 크기", example = "20")
      @RequestParam(defaultValue = "20") int size,

      @Parameter(description = "정렬 (field,direction)", example = "date,asc")
      @RequestParam(defaultValue = "date,asc") String sort
  ) {
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

    Page<Holiday> holidays = searchHolidaysUseCase.search(query);
    Page<HolidayResponseDto> response = holidays.map(HolidayResponseDto::from);

    return ResponseEntity.ok(response);
  }
}
