package com.planitsquare.miniservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.exception.CountryNotFoundException;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysCommand;
import com.planitsquare.miniservice.application.port.in.RefreshHolidayDto;
import com.planitsquare.miniservice.application.port.in.RefreshHolidaysCommand;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("HolidayManagementService 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayManagementServiceTest {

  @Mock private FindCountryPort findCountryPort;
  @Mock private DeleteHolidaysPort deleteHolidaysPort;
  @Mock private FetchHolidaysPort fetchHolidaysPort;
  @Mock private SaveAllHolidaysPort saveAllHolidaysPort;
  @Mock private SyncJobValidator syncJobValidator;

  @InjectMocks private HolidayManagementService holidayManagementService;

  private Country KR;
  private Country US;
  private Holiday sampleHoliday;

  @BeforeEach
  void setUp() {
    KR = new Country(new CountryCode("KR"), "South Korea");
    US = new Country(new CountryCode("US"), "United States");

    sampleHoliday = new Holiday(
        new HolidayId(1L),
        KR,
        "설날",
        "Lunar New Year",
        LocalDate.of(2025, 1, 1),
        new HolidayMetadata(true, true, 2000, List.of("Public"), List.of())
    );
  }

  @Nested
  @DisplayName("deleteHolidays 테스트")
  class DeleteHolidaysTest {

    @Test
    @DisplayName("특정 연도와 국가의 공휴일을 삭제하고 삭제 건수를 반환한다")
    void 특정_연도와_국가의_공휴일을_삭제하고_삭제_건수를_반환한다() {
      // Given
      int year = 2024;
      CountryCode countryCode = new CountryCode("KR");
      DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, countryCode);

      given(findCountryPort.existsByCode(countryCode.code())).willReturn(true);
      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(10);

      // When
      int deletedCount = holidayManagementService.deleteHolidays(command);

      // Then
      assertThat(deletedCount).isEqualTo(10);
      then(syncJobValidator).should().validateNoRunningJob();
      then(findCountryPort).should().existsByCode(countryCode.code());
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
    }

    @Test
    @DisplayName("삭제할 데이터가 없으면 0을 반환한다")
    void 삭제할_데이터가_없으면_0을_반환한다() {
      // Given
      int year = 2024;
      CountryCode countryCode = new CountryCode("JP");
      DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, countryCode);

      given(findCountryPort.existsByCode(countryCode.code())).willReturn(true);
      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(0);

      // When
      int deletedCount = holidayManagementService.deleteHolidays(command);

      // Then
      assertThat(deletedCount).isEqualTo(0);
      then(syncJobValidator).should().validateNoRunningJob();
      then(findCountryPort).should().existsByCode(countryCode.code());
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
    }
  }

  @Nested
  @DisplayName("refreshHolidays 테스트")
  class RefreshHolidaysTest {

    @Test
    @DisplayName("특정 연도와 국가의 공휴일을 삭제하고 새로 조회하여 저장한다")
    void 특정_연도와_국가의_공휴일을_삭제하고_새로_조회하여_저장한다() {
      // Given
      List<Holiday> holidays = List.of(sampleHoliday, sampleHoliday, sampleHoliday);

      int year = 2024;
      CountryCode countryCode = new CountryCode("KR");
      RefreshHolidaysCommand command = new RefreshHolidaysCommand(
          year,
          countryCode,
          SyncExecutionType.API_REFRESH,
          holidays
      );

      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(5);

      // When
      RefreshHolidayDto result = holidayManagementService.refreshHolidays(command);

      // Then
      assertThat(result.deleteCount()).isEqualTo(5);
      assertThat(result.insertCount()).isEqualTo(3);
      then(syncJobValidator).should().validateNoRunningJob();
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
      then(saveAllHolidaysPort).should().saveAllHolidays(holidays);
    }

    @Test
    @DisplayName("삭제된 데이터가 없어도 새로운 데이터를 조회하여 저장한다")
    void 삭제된_데이터가_없어도_새로운_데이터를_조회하여_저장한다() {
      // Given
      int year = 2024;
      CountryCode countryCode = new CountryCode("US");
      List<Holiday> holidays = List.of(sampleHoliday);
      RefreshHolidaysCommand command = new RefreshHolidaysCommand(
          year,
          countryCode,
          SyncExecutionType.API_REFRESH,
          holidays
      );

      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(0);

      // When
      RefreshHolidayDto result = holidayManagementService.refreshHolidays(command);

      // Then
      assertThat(result.deleteCount()).isEqualTo(0);
      assertThat(result.insertCount()).isEqualTo(1);
      then(syncJobValidator).should().validateNoRunningJob();
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
      then(saveAllHolidaysPort).should().saveAllHolidays(holidays);
    }

    @Test
    @DisplayName("빈 공휴일 목록으로 리프레시를 수행한다")
    void 빈_공휴일_목록으로_리프레시를_수행한다() {
      // Given
      int year = 2024;
      CountryCode countryCode = new CountryCode("XX");
      RefreshHolidaysCommand command = new RefreshHolidaysCommand(
          year,
          countryCode,
          SyncExecutionType.API_REFRESH,
          Collections.emptyList()
      );

      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(0);

      // When
      RefreshHolidayDto result = holidayManagementService.refreshHolidays(command);

      // Then
      assertThat(result.deleteCount()).isEqualTo(0);
      assertThat(result.insertCount()).isEqualTo(0);
      then(syncJobValidator).should().validateNoRunningJob();
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
      then(saveAllHolidaysPort).should().saveAllHolidays(Collections.emptyList());
    }
  }
}
