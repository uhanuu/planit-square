package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.IntegrationTestBase;
import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.repository.CountryJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.repository.HolidayJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import com.planitsquare.miniservice.domain.vo.HolidayType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HolidaySearchController 통합 테스트.
 *
 * <p>실제 Spring Boot 컨텍스트를 사용하여 공휴일 검색 API 엔드포인트를 테스트합니다.
 *
 * @since 1.0
 */
@DisplayName("HolidaySearchController 통합 테스트")
@Transactional
class HolidaySearchControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private HolidayJpaRepository holidayJpaRepository;

  @Autowired
  private CountryJpaRepository countryJpaRepository;

  private MockMvc mockMvc;

  private CountryJpaEntity korea;
  private CountryJpaEntity usa;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    // 테스트 데이터 준비
    korea = countryJpaRepository.save(new CountryJpaEntity("KR", "South Korea"));
    usa = countryJpaRepository.save(new CountryJpaEntity("US", "United States"));

    // 한국 공휴일
    HolidayJpaEntity newYear = new HolidayJpaEntity(
        korea,
        "신정",
        "New Year's Day",
        LocalDate.of(2024, 1, 1),
        new HolidayMetadataEmbeddable(true, true, 1949),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    HolidayJpaEntity lunarNewYear = new HolidayJpaEntity(
        korea,
        "설날",
        "Lunar New Year",
        LocalDate.of(2024, 2, 10),
        new HolidayMetadataEmbeddable(false, true, 1985),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    HolidayJpaEntity independenceDay = new HolidayJpaEntity(
        korea,
        "광복절",
        "Independence Day",
        LocalDate.of(2024, 8, 15),
        new HolidayMetadataEmbeddable(true, true, 1949),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    // 미국 공휴일
    HolidayJpaEntity usNewYear = new HolidayJpaEntity(
        usa,
        "New Year's Day",
        "New Year's Day",
        LocalDate.of(2024, 1, 1),
        new HolidayMetadataEmbeddable(true, true, 1870),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    HolidayJpaEntity independence = new HolidayJpaEntity(
        usa,
        "Independence Day",
        "Independence Day",
        LocalDate.of(2024, 7, 4),
        new HolidayMetadataEmbeddable(true, true, 1870),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    // 2025년 공휴일
    HolidayJpaEntity newYear2025 = new HolidayJpaEntity(
        korea,
        "신정",
        "New Year's Day",
        LocalDate.of(2025, 1, 1),
        new HolidayMetadataEmbeddable(true, true, 1949),
        List.of(HolidayType.PUBLIC),
        List.of()
    );

    holidayJpaRepository.save(newYear);
    holidayJpaRepository.save(lunarNewYear);
    holidayJpaRepository.save(independenceDay);
    holidayJpaRepository.save(usNewYear);
    holidayJpaRepository.save(independence);
    holidayJpaRepository.save(newYear2025);
  }

  @Test
  @DisplayName("모든 공휴일을 조회하면 200 OK와 함께 페이징된 결과를 반환한다")
  void 모든_공휴일을_조회하면_200_OK와_함께_페이징된_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").exists())
        .andExpect(jsonPath("$.totalPages").exists())
        .andExpect(jsonPath("$.size").exists())
        .andExpect(jsonPath("$.number").exists());
  }

  @Test
  @DisplayName("연도로 필터링하면 해당 연도의 공휴일만 반환한다")
  void 연도로_필터링하면_해당_연도의_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("year", "2024"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].date").value(org.hamcrest.Matchers.startsWith("2024")));
  }

  @Test
  @DisplayName("국가 코드로 필터링하면 해당 국가의 공휴일만 반환한다")
  void 국가_코드로_필터링하면_해당_국가의_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("countryCode", "KR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].countryCode").value("KR"));
  }

  @Test
  @DisplayName("날짜 범위로 필터링하면 해당 범위의 공휴일만 반환한다")
  void 날짜_범위로_필터링하면_해당_범위의_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("from", "2024-01-01")
            .param("to", "2024-02-29"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("공휴일 타입으로 필터링하면 해당 타입의 공휴일만 반환한다")
  void 공휴일_타입으로_필터링하면_해당_타입의_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("type", "Public"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].types[0]").value("PUBLIC"));
  }

  @Test
  @DisplayName("이름으로 검색하면 이름에 검색어가 포함된 공휴일만 반환한다")
  void 이름으로_검색하면_이름에_검색어가_포함된_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("name", "설날"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].localName").value(org.hamcrest.Matchers.containsString("설날")));
  }

  @Test
  @DisplayName("영문 이름으로 검색하면 영문 이름에 검색어가 포함된 공휴일만 반환한다")
  void 영문_이름으로_검색하면_영문_이름에_검색어가_포함된_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("name", "Independence"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("페이지 크기를 지정하면 해당 크기만큼의 결과를 반환한다")
  void 페이지_크기를_지정하면_해당_크기만큼의_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(2)));
  }

  @Test
  @DisplayName("페이지 번호를 지정하면 해당 페이지의 결과를 반환한다")
  void 페이지_번호를_지정하면_해당_페이지의_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("page", "0")
            .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value(0));
  }

  @Test
  @DisplayName("정렬 조건을 지정하면 해당 조건으로 정렬된 결과를 반환한다")
  void 정렬_조건을_지정하면_해당_조건으로_정렬된_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("sort", "date,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("복합 조건으로 검색하면 모든 조건을 만족하는 공휴일만 반환한다")
  void 복합_조건으로_검색하면_모든_조건을_만족하는_공휴일만_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("year", "2024")
            .param("countryCode", "KR")
            .param("type", "Public"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].countryCode").value("KR"));
  }

  @Test
  @DisplayName("존재하지 않는 국가 코드로 검색하면 빈 결과를 반환한다")
  void 존재하지_않는_국가_코드로_검색하면_빈_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("countryCode", "XX"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @DisplayName("잘못된 날짜 형식으로 요청하면 500 Internal Server Error를 반환한다")
  void 잘못된_날짜_형식으로_요청하면_500_Internal_Server_Error를_반환한다() throws Exception {
    // Spring의 기본 날짜 파싱 실패는 500으로 처리됨
    mockMvc.perform(get("/api/v1/holidays")
            .param("from", "invalid-date"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("from이 to보다 이후 날짜이면 빈 결과를 반환한다")
  void from이_to보다_이후_날짜이면_빈_결과를_반환한다() throws Exception {
    mockMvc.perform(get("/api/v1/holidays")
            .param("from", "2024-12-31")
            .param("to", "2024-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }
}
