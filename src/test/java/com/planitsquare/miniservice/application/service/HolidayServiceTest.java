package com.planitsquare.miniservice.application.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.out.FetchCountriesPort;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllCountriesPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
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

  @Mock
  private FindCountryPort findCountryPort;

  @Mock
  private FetchHolidaysPort fetchHolidaysPort;

  @Mock
  private FetchCountriesPort fetchCountriesPort;

  @Mock
  private SaveAllCountriesPort saveAllCountriesPort;

  @Mock
  private SaveAllHolidaysPort saveAllHolidaysPort;

  @InjectMocks
  private HolidayService holidayService;

  private Country testCountryKR;
  private Country testCountryUS;
  private List<Country> testCountries;
  private Holiday testHoliday;

  @BeforeEach
  void setUp() {
    testCountryKR = new Country(new CountryCode("KR"), "South Korea");
    testCountryUS = new Country(new CountryCode("US"), "United States");
    testCountries = List.of(testCountryKR, testCountryUS);

    testHoliday = new Holiday(
        new HolidayId(1L),
        testCountryKR,
        "설날",
        "Lunar New Year",
        LocalDate.of(2025, 1, 1),
        new HolidayMetadata(true, true, 2000, List.of("Public"), List.of())
    );
  }

  @Nested
  @DisplayName("uploadHolidays 테스트")
  class UploadHolidaysTest {

    @Test
    @DisplayName("INITIAL_SYSTEM_LOAD 타입이면 외부 API에서 국가를 조회하고 저장한다")
    void INITIAL_SYSTEM_LOAD_타입이면_외부_API에서_국가를_조회하고_저장한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.INITIAL_SYSTEM_LOAD);
      given(fetchCountriesPort.fetchCountries()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(fetchCountriesPort).should(times(1)).fetchCountries();
      then(saveAllCountriesPort).should(times(1)).saveAllCountries(testCountries);
      then(findCountryPort).should(never()).findAll();
    }

    @Test
    @DisplayName("INITIAL_SYSTEM_LOAD가 아니면 DB에서 국가를 조회한다")
    void INITIAL_SYSTEM_LOAD가_아니면_DB에서_국가를_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(findCountryPort).should(times(1)).findAll();
      then(fetchCountriesPort).should(never()).fetchCountries();
      then(saveAllCountriesPort).should(never()).saveAllCountries(anyList());
    }

    @Test
    @DisplayName("연도 범위 내 모든 연도에 대해 공휴일을 조회하고 저장한다")
    void 연도_범위_내_모든_연도에_대해_공휴일을_조회하고_저장한다() {
      // Given
      int endYear = 2025;
      UploadHolidayCommand command = new UploadHolidayCommand(endYear, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then - 2021~2025 (5년) x 2개 국가 = 10번 호출
      then(fetchHolidaysPort).should(times(10)).fetchHolidays(anyInt(), any(Country.class));
      then(saveAllHolidaysPort).should(times(10)).saveAllHolidays(anyList());
    }

    @Test
    @DisplayName("각 국가별로 공휴일을 조회한다")
    void 각_국가별로_공휴일을_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then - 각 국가에 대해 5년치 조회
      then(fetchHolidaysPort).should(times(5)).fetchHolidays(anyInt(), eq(testCountryKR));
      then(fetchHolidaysPort).should(times(5)).fetchHolidays(anyInt(), eq(testCountryUS));
    }

    @Test
    @DisplayName("국가 목록이 비어있어도 예외가 발생하지 않는다")
    void 국가_목록이_비어있어도_예외가_발생하지_않는다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(List.of());

      // When & Then
      assertThatCode(() -> holidayService.uploadHolidays(command))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("연도 범위 검증 테스트")
  class YearRangeValidationTest {

    @Test
    @DisplayName("2025년 입력 시 2021~2025년 공휴일을 조회한다")
    void year_2025_입력_시_2021_2025년_공휴일을_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(List.of(testCountryKR));
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(fetchHolidaysPort).should().fetchHolidays(2021, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2022, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2023, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2024, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2025, testCountryKR);
    }

    @Test
    @DisplayName("2004년 입력 시 2000~2004년 공휴일을 조회한다")
    void year_2004_입력_시_2000_2004년_공휴일을_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2004, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(List.of(testCountryKR));
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(fetchHolidaysPort).should().fetchHolidays(2000, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2001, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2002, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2003, testCountryKR);
      then(fetchHolidaysPort).should().fetchHolidays(2004, testCountryKR);
    }
  }

  @Nested
  @DisplayName("다양한 SyncExecutionType 테스트")
  class SyncExecutionTypeTest {

    @Test
    @DisplayName("SCHEDULED_BATCH 타입이면 DB에서 국가를 조회한다")
    void SCHEDULED_BATCH_타입이면_DB에서_국가를_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.SCHEDULED_BATCH);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(findCountryPort).should().findAll();
      then(fetchCountriesPort).should(never()).fetchCountries();
    }

    @Test
    @DisplayName("API_REFRESH 타입이면 DB에서 국가를 조회한다")
    void API_REFRESH_타입이면_DB에서_국가를_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.API_REFRESH);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(findCountryPort).should().findAll();
      then(fetchCountriesPort).should(never()).fetchCountries();
    }

    @Test
    @DisplayName("MANUAL_EXECUTION 타입이면 DB에서 국가를 조회한다")
    void MANUAL_EXECUTION_타입이면_DB에서_국가를_조회한다() {
      // Given
      UploadHolidayCommand command = new UploadHolidayCommand(2025, SyncExecutionType.MANUAL_EXECUTION);
      given(findCountryPort.findAll()).willReturn(testCountries);
      given(fetchHolidaysPort.fetchHolidays(anyInt(), any(Country.class)))
          .willReturn(List.of(testHoliday));

      // When
      holidayService.uploadHolidays(command);

      // Then
      then(findCountryPort).should().findAll();
      then(fetchCountriesPort).should(never()).fetchCountries();
    }
  }
}
