package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.request.RefreshHolidayRequest;
import com.planitsquare.miniservice.adapter.in.web.dto.request.UploadHolidayRequest;
import com.planitsquare.miniservice.adapter.in.web.dto.response.RefreshHolidayResponse;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.FetchHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.RefreshHolidayDto;
import com.planitsquare.miniservice.application.port.in.RefreshHolidaysCommand;
import com.planitsquare.miniservice.application.port.in.RefreshHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.domain.model.Holiday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("HolidayController 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayControllerTest {

  @Mock
  private UploadHolidaysUseCase uploadHolidaysUseCase;

  @Mock
  private RefreshHolidaysUseCase refreshHolidaysUseCase;

  @Mock
  private DeleteHolidaysUseCase deleteHolidaysUseCase;

  @Mock
  private FetchHolidaysUseCase fetchHolidaysUseCase;

  @InjectMocks
  private HolidayController controller;

  @Test
  @DisplayName("유효한 요청으로 공휴일 업로드를 수행한다")
  void 유효한_요청으로_공휴일_업로드를_수행한다() {
    // Given
    UploadHolidayRequest request = new UploadHolidayRequest(2025);

    // When
    controller.uploadHolidays(request);

    // Then
    verify(uploadHolidaysUseCase).uploadHolidays(any(UploadHolidayCommand.class));
  }

  @Test
  @DisplayName("유효한 요청으로 공휴일 갱신을 수행한다")
  void 유효한_요청으로_공휴일_갱신을_수행한다() {
    // Given
    RefreshHolidayRequest request = new RefreshHolidayRequest(2024, "KR");
    RefreshHolidayDto dto = new RefreshHolidayDto(5, 10);
    given(fetchHolidaysUseCase.fetchHolidays(anyInt(), any()))
        .willReturn(Collections.emptyList());
    given(refreshHolidaysUseCase.refreshHolidays(any(RefreshHolidaysCommand.class)))
        .willReturn(dto);

    // When
    ResponseEntity<RefreshHolidayResponse> response = controller.refreshHolidays(request);

    // Then
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().deleteCount()).isEqualTo(5);
    assertThat(response.getBody().insertCount()).isEqualTo(10);
    verify(fetchHolidaysUseCase).fetchHolidays(anyInt(), any());
    verify(refreshHolidaysUseCase).refreshHolidays(any(RefreshHolidaysCommand.class));
  }
}
