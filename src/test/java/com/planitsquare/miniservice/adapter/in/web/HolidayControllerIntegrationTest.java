package com.planitsquare.miniservice.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.miniservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HolidayController 통합 테스트.
 *
 * <p>실제 Spring Boot 컨텍스트를 사용하여 REST API 엔드포인트를 테스트합니다.
 *
 * @since 1.0
 */
@DisplayName("HolidayController 통합 테스트")
class HolidayControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("유효한 요청으로 공휴일 업로드를 수행하면 202 ACCEPTED를 반환한다")
  void 유효한_요청으로_공휴일_업로드를_수행하면_202_ACCEPTED를_반환한다() throws Exception {
    // Given
    UploadHolidayRequest request = new UploadHolidayRequest(2025);

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted());
  }

  @Test
  @DisplayName("최소 연도보다 작은 연도로 요청 시 500 Internal Server Error를 반환한다")
  void 최소_연도보다_작은_연도로_요청시_500_Internal_Server_Error를_반환한다() throws Exception {
    // Given - compact constructor에서 발생하는 예외는 deserialization 과정에서 500으로 처리됨
    String invalidRequest = """
        {
          "year": 1999
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidRequest))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("year 필드가 누락된 요청 시 500 Internal Server Error를 반환한다")
  void year_필드가_누락된_요청시_500_Internal_Server_Error를_반환한다() throws Exception {
    // Given - 필수 필드 누락은 deserialization 과정에서 500으로 처리됨
    String invalidRequest = "{}";

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidRequest))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("잘못된 JSON 형식으로 요청 시 500 Internal Server Error를 반환한다")
  void 잘못된_JSON_형식으로_요청시_500_Internal_Server_Error를_반환한다() throws Exception {
    // Given - JSON 파싱 에러는 500으로 처리됨
    String invalidJson = "invalid json";

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Content-Type이 없는 요청 시 500 Internal Server Error를 반환한다")
  void ContentType이_없는_요청시_500_Internal_Server_Error를_반환한다() throws Exception {
    // Given - Content-Type 누락은 500으로 처리됨
    UploadHolidayRequest request = new UploadHolidayRequest(2025);

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }
}
