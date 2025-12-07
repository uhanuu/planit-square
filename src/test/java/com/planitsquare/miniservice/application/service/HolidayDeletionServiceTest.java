package com.planitsquare.miniservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.planitsquare.miniservice.application.port.in.DeleteHolidaysCommand;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("HolidayDeletionService 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayDeletionServiceTest {

  @Mock
  private DeleteHolidaysPort deleteHolidaysPort;

  @InjectMocks
  private HolidayDeletionService holidayDeletionService;

  @Test
  @DisplayName("특정 연도와 국가의 공휴일을 삭제하고 삭제 건수를 반환한다")
  void 공휴일_삭제_성공() {
    // Given
    int year = 2024;
    CountryCode countryCode = new CountryCode("KR");
    DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, countryCode);

    given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
        .willReturn(10);

    // When
    int deletedCount = holidayDeletionService.deleteHolidays(command);

    // Then
    assertThat(deletedCount).isEqualTo(10);
    then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
  }

  @Test
  @DisplayName("삭제할 데이터가 없으면 0을 반환한다")
  void 삭제할_데이터_없음() {
    // Given
    int year = 2024;
    CountryCode countryCode = new CountryCode("JP");
    DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, countryCode);

    given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
        .willReturn(0);

    // When
    int deletedCount = holidayDeletionService.deleteHolidays(command);

    // Then
    assertThat(deletedCount).isEqualTo(0);
    then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
  }
}
