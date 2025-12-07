package com.planitsquare.miniservice.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.miniservice.adapter.in.web.dto.request.UploadHolidayRequest;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.RefreshHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HolidayController 통합 테스트.
 * Job이 이미 실행되는 경우 테스트가 깨질 수 있어 Mock으로 필요한 부분만 검증
 *
 * <p>실제 Spring Boot 컨텍스트를 사용하여 REST API 엔드포인트를 테스트합니다.
 *
 * @since 1.0
 */
@DisplayName("HolidayController 통합 테스트")
class HolidayControllerIntegrationWithMockTest extends MockIntegrationTestBase {

  @Autowired
  private WebApplicationContext context;
  @MockitoBean
  private UploadHolidaysUseCase uploadHolidaysUseCase;
  @MockitoBean
  private DeleteHolidaysUseCase deleteHolidaysUseCase;
  @MockitoBean
  private RefreshHolidaysUseCase refreshHolidaysUseCase;
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
    // job이 이미 실행되는 경우 깨질 수 있어 Mock 처리
    doNothing().when(uploadHolidaysUseCase).uploadHolidays(any());

    // When & Then
    mockMvc.perform(post("/api/v1/holidays")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted());
  }

}
