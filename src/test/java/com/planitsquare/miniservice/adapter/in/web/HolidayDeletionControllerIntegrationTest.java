package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.IntegrationTestBase;
import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.repository.CountryJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.repository.HolidayJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HolidayController 삭제 API 통합 테스트.
 *
 * <p>실제 Spring Boot 컨텍스트와 데이터베이스를 사용하여 삭제 API를 테스트합니다.
 *
 * @since 1.0
 */
@DisplayName("HolidayController 삭제 API 통합 테스트")
@Transactional
class HolidayDeletionControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private HolidayJpaRepository holidayJpaRepository;

  @Autowired
  private CountryJpaRepository countryJpaRepository;

  private MockMvc mockMvc;
  private CountryJpaEntity krCountry;
  private CountryJpaEntity usCountry;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    // 테스트 데이터 초기화
    holidayJpaRepository.deleteAll();
    countryJpaRepository.deleteAll();

    // 국가 데이터 생성
    krCountry = countryJpaRepository.save(new CountryJpaEntity("KR", "South Korea"));
    usCountry = countryJpaRepository.save(new CountryJpaEntity("US", "United States"));

    // 공휴일 데이터 생성
    createHolidays();
  }

  private void createHolidays() {
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(
        true, true, 2000
    );

    // 한국 2024년 공휴일 3개
    holidayJpaRepository.save(new HolidayJpaEntity(
        krCountry, "신정", "New Year's Day",
        LocalDate.of(2024, 1, 1), metadata, List.of(), List.of()
    ));
    holidayJpaRepository.save(new HolidayJpaEntity(
        krCountry, "설날", "Lunar New Year",
        LocalDate.of(2024, 2, 10), metadata, List.of(), List.of()
    ));
    holidayJpaRepository.save(new HolidayJpaEntity(
        krCountry, "광복절", "Liberation Day",
        LocalDate.of(2024, 8, 15), metadata, List.of(), List.of()
    ));

    // 한국 2025년 공휴일 2개
    holidayJpaRepository.save(new HolidayJpaEntity(
        krCountry, "신정", "New Year's Day",
        LocalDate.of(2025, 1, 1), metadata, List.of(), List.of()
    ));
    holidayJpaRepository.save(new HolidayJpaEntity(
        krCountry, "설날", "Lunar New Year",
        LocalDate.of(2025, 1, 29), metadata, List.of(), List.of()
    ));

    // 미국 2024년 공휴일 2개
    holidayJpaRepository.save(new HolidayJpaEntity(
        usCountry, "New Year's Day", "New Year's Day",
        LocalDate.of(2024, 1, 1), metadata, List.of(), List.of()
    ));
    holidayJpaRepository.save(new HolidayJpaEntity(
        usCountry, "Independence Day", "Independence Day",
        LocalDate.of(2024, 7, 4), metadata, List.of(), List.of()
    ));
  }

  @Test
  @DisplayName("특정 연도와 국가의 공휴일을 삭제하고 삭제 건수를 반환한다")
  void 특정_연도와_국가의_공휴일을_삭제하고_삭제_건수를_반환한다() throws Exception {
    // Given
    long beforeCount = holidayJpaRepository.count();
    assertThat(beforeCount).isEqualTo(7); // 총 7개

    // When & Then
    mockMvc.perform(delete("/api/v1/holidays/2024/KR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.deletedCount").value(3));

    // 삭제 후 확인
    long afterCount = holidayJpaRepository.count();
    assertThat(afterCount).isEqualTo(4); // 7 - 3 = 4개 남음
  }

  @Test
  @DisplayName("삭제할 데이터가 없으면 0을 반환한다")
  void 삭제할_데이터가_없으면_0을_반환한다() throws Exception {
    // Given - 2026년 데이터는 없음
    long beforeCount = holidayJpaRepository.count();

    // When & Then
    mockMvc.perform(delete("/api/v1/holidays/2026/KR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.deletedCount").value(0));

    // 데이터 변화 없음
    long afterCount = holidayJpaRepository.count();
    assertThat(afterCount).isEqualTo(beforeCount);
  }

  @Test
  @DisplayName("다른 연도와 국가의 데이터는 삭제되지 않는다")
  void 다른_연도와_국가의_데이터는_삭제되지_않는다() throws Exception {
    // When - 한국 2024년 데이터만 삭제
    mockMvc.perform(delete("/api/v1/holidays/2024/KR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.deletedCount").value(3));

    // Then - 한국 2025년, 미국 2024년 데이터는 남아있음
    long remainingCount = holidayJpaRepository.count();
    assertThat(remainingCount).isEqualTo(4); // 2(KR 2025) + 2(US 2024)
  }

  @Test
  @DisplayName("유효하지 않은 연도 타입으로 요청 시 500 Internal Server Error를 반환한다")
  void 유효하지_않은_연도_타입으로_요청시_500_Internal_Server_Error를_반환한다() throws Exception {
    // When & Then - 연도에 문자열 전달 시 타입 변환 실패로 500 반환
    mockMvc.perform(delete("/api/v1/holidays/invalid/KR"))
        .andExpect(status().isInternalServerError());
  }
}
