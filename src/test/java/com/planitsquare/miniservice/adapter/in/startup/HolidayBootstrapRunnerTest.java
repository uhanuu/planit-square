package com.planitsquare.miniservice.adapter.in.startup;

import com.planitsquare.miniservice.application.port.in.CheckInitialSystemLoadUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@DisplayName("HolidayBootstrapRunner 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayBootstrapRunnerTest {

  @Mock
  private CheckInitialSystemLoadUseCase checkInitialSystemLoadUseCase;

  @Mock
  private UploadHolidaysUseCase uploadHolidaysUseCase;

  @Mock
  private ApplicationArguments args;

  @InjectMocks
  private HolidayBootstrapRunner runner;

  @Test
  @DisplayName("최초 실행 시 공휴일 업로드를 수행한다")
  void 최초_실행시_공휴일_업로드를_수행한다() {
    // Given
    given(checkInitialSystemLoadUseCase.isInitialSystemLoad()).willReturn(true);

    // When
    runner.run(args);

    // Then
    then(uploadHolidaysUseCase).should(times(1)).uploadHolidays(any());
  }

  @Test
  @DisplayName("최초 실행이 아닐 경우 공휴일 업로드를 수행하지 않는다")
  void 최초_실행이_아닐_경우_공휴일_업로드를_수행하지_않는다() {
    // Given
    given(checkInitialSystemLoadUseCase.isInitialSystemLoad()).willReturn(false);

    // When
    runner.run(args);

    // Then
    then(uploadHolidaysUseCase).should(never()).uploadHolidays(any());
  }
}
