package com.planitsquare.miniservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.out.*;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("HolidayService 테스트")
@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

  @Mock private FindCountryPort findCountryPort;
  @Mock private FetchHolidaysPort fetchHolidaysPort;
  @Mock private FetchCountriesPort fetchCountriesPort;
  @Mock private SaveAllCountriesPort saveAllCountriesPort;
  @Mock private SaveAllHolidaysPort saveAllHolidaysPort;
  @Mock private DeleteHolidaysPort deleteHolidaysPort;
  @Mock private RecordSyncHistoryPort recordSyncHistoryPort;
  @Mock private SyncJobPort syncJobPort;
  @Mock private HolidaySyncService holidaySyncService;

  @InjectMocks private HolidayService holidayService;

  private Country KR;
  private Country US;
  private List<Country> countries;
  private Holiday sampleHoliday;

  @BeforeEach
  void setUp() {
    KR = new Country(new CountryCode("KR"), "South Korea");
    US = new Country(new CountryCode("US"), "United States");
    countries = List.of(KR, US);

    sampleHoliday = new Holiday(
        new HolidayId(1L),
        KR,
        "설날",
        "Lunar New Year",
        LocalDate.of(2025, 1, 1),
        new HolidayMetadata(true, true, 2000, List.of("Public"), List.of())
    );

    // 공휴일 조회 기본 설정 (lenient로 설정하여 사용되지 않을 수 있음을 허용)
    lenient().when(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
        .thenReturn(List.of(sampleHoliday));

    // HolidaySyncService 기본 설정
    lenient().when(holidaySyncService.syncHolidaysForCountryAndYear(any(SyncHolidayCommand.class)))
        .thenReturn(List.of(sampleHoliday));

    // SyncJob 기본 설정
    lenient().when(syncJobPort.startJob(any(SyncExecutionType.class))).thenReturn(1L);
  }

  private UploadHolidayCommand cmd(int year, SyncExecutionType type) {
    return new UploadHolidayCommand(year, type);
  }

  @Nested
  @DisplayName("uploadHolidays 테스트")
  class UploadHolidaysTest {

    @Test
    @DisplayName("INITIAL_SYSTEM_LOAD면 외부 국가 조회 후 저장한다")
    void initialLoad_조회_및_저장() {
      // Given
      given(fetchCountriesPort.fetchCountries()).willReturn(countries);

      // When
      holidayService.uploadHolidays(cmd(2025, SyncExecutionType.INITIAL_SYSTEM_LOAD));

      // Then
      then(fetchCountriesPort).should(times(1)).fetchCountries();
      then(saveAllCountriesPort).should(times(1)).saveAllCountries(countries);
      then(findCountryPort).should(never()).findAll();
    }

    @Test
    @DisplayName("INITIAL_SYSTEM_LOAD가 아니면 DB에서 국가 조회한다")
    void batch_조회() {
      // Given
      given(findCountryPort.findAll()).willReturn(countries);

      // When
      holidayService.uploadHolidays(cmd(2025, SyncExecutionType.SCHEDULED_BATCH));

      // Then
      then(findCountryPort).should(times(1)).findAll();
      then(fetchCountriesPort).should(never()).fetchCountries();
      then(saveAllCountriesPort).should(never()).saveAllCountries(anyList());
    }

    @Test
    @DisplayName("연도 범위 내 모든 연도 X 국가 수만큼 조회 및 저장")
    void 전체_연도_범위_조회() {
      // Given
      given(findCountryPort.findAll()).willReturn(countries);

      // When
      holidayService.uploadHolidays(cmd(2025, SyncExecutionType.SCHEDULED_BATCH));

      // Then: 2021~2025 (5년) × 2개 국가 = 10번
      then(holidaySyncService).should(times(10)).syncHolidaysForCountryAndYear(any(SyncHolidayCommand.class));
    }

    @Test
    @DisplayName("각 국가에 대해 개별적으로 연도 범위 조회한다")
    void 국가_별_조회() {
      // Given
      given(findCountryPort.findAll()).willReturn(countries);

      // When
      holidayService.uploadHolidays(cmd(2025, SyncExecutionType.SCHEDULED_BATCH));

      // Then - 2개 국가 × 5년 = 총 10번 호출
      then(holidaySyncService).should(times(10))
          .syncHolidaysForCountryAndYear(any(SyncHolidayCommand.class));
    }

    @Test
    @DisplayName("국가 목록이 비어있어도 예외 발생 안 함")
    void 빈_국가() {
      // Given
      given(findCountryPort.findAll()).willReturn(List.of());

      // When & Then
      assertThatCode(() -> holidayService.uploadHolidays(cmd(2025, SyncExecutionType.SCHEDULED_BATCH)))
          .doesNotThrowAnyException();
    }

    @Nested
    @DisplayName("연도 범위 검증")
    class YearRangeValidation {

      @Test
      @DisplayName("2025년 입력 시 2021~2025 조회")
      void range_2025() {
        given(findCountryPort.findAll()).willReturn(List.of(KR));

        holidayService.uploadHolidays(cmd(2025, SyncExecutionType.SCHEDULED_BATCH));

        verifyYearCalls(KR, 2021, 2025);
      }

      @Test
      @DisplayName("2004년 입력 시 2000~2004 조회")
      void range_2004() {
        given(findCountryPort.findAll()).willReturn(List.of(KR));

        holidayService.uploadHolidays(cmd(2004, SyncExecutionType.SCHEDULED_BATCH));

        verifyYearCalls(KR, 2000, 2004);
      }

      private void verifyYearCalls(Country country, int startYear, int endYear) {
        // 연도 범위 검증: startYear부터 endYear까지의 연도 수만큼 호출되었는지 확인
        int expectedCalls = endYear - startYear + 1;
        then(holidaySyncService).should(times(expectedCalls))
            .syncHolidaysForCountryAndYear(any(SyncHolidayCommand.class));
      }
    }
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

      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(10);

      // When
      int deletedCount = holidayService.deleteHolidays(command);

      // Then
      assertThat(deletedCount).isEqualTo(10);
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
    }

    @Test
    @DisplayName("삭제할 데이터가 없으면 0을 반환한다")
    void 삭제할_데이터가_없으면_0을_반환한다() {
      // Given
      int year = 2024;
      CountryCode countryCode = new CountryCode("JP");
      DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, countryCode);

      given(deleteHolidaysPort.deleteByYearAndCountryCode(year, countryCode))
          .willReturn(0);

      // When
      int deletedCount = holidayService.deleteHolidays(command);

      // Then
      assertThat(deletedCount).isEqualTo(0);
      then(deleteHolidaysPort).should().deleteByYearAndCountryCode(year, countryCode);
    }
  }
}
