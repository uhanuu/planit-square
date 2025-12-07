package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.request.UploadHolidayRequest;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DisplayName("HolidayController 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayControllerTest {

  @Mock
  private UploadHolidaysUseCase uploadHolidaysUseCase;

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
}
