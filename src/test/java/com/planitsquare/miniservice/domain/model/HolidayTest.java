package com.planitsquare.miniservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Holiday Entity 테스트")
class HolidayTest {

  @Test
  @DisplayName("Holiday가 정상적으로 생성된다")
  void Holiday가_정상적으로_생성된다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("KR", "South Korea");
    String localName = "설날";
    String name = "Lunar New Year";
    LocalDate date = LocalDate.of(2025, 1, 29);
    List<String> regions = List.of("서울", "부산");
    HolidayMetadata metadata = new HolidayMetadata(true, true, 1949, List.of("Public", "National"), regions);

    // When
    Holiday holiday = new Holiday(id, country, localName, name, date, metadata);

    // Then
    assertThat(holiday).isNotNull();
    assertThat(holiday.getId()).isEqualTo(id);
    assertThat(holiday.getCountry()).isEqualTo(country);
    assertThat(holiday.getLocalName()).isEqualTo("설날");
    assertThat(holiday.getName()).isEqualTo("Lunar New Year");
    assertThat(holiday.getDate()).isEqualTo(date);
    assertThat(holiday.getMetadata().applicableRegions()).containsExactly("서울", "부산");
  }

  @Test
  @DisplayName("Holiday의 메타데이터를 올바르게 조회한다")
  void Holiday의_메타데이터를_올바르게_조회한다() {
    // Given
    HolidayId id = new HolidayId(2L);
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(true, false, 2020, List.of("Public"), null);
    Holiday holiday =
        new Holiday(id, country, "임시공휴일", "Temporary Holiday", LocalDate.of(2025, 5, 6), metadata);

    // When
    HolidayMetadata retrievedMetadata = holiday.getMetadata();

    // Then
    assertThat(retrievedMetadata).isEqualTo(metadata);
    assertThat(retrievedMetadata.fixed()).isTrue();
    assertThat(retrievedMetadata.global()).isFalse();
    assertThat(retrievedMetadata.launchYear()).isEqualTo(2020);
    assertThat(retrievedMetadata.types()).containsExactly("Public");
  }

  @Test
  @DisplayName("Holiday는 불변 객체다")
  void Holiday는_불변_객체다() {
    // Given
    HolidayId id = new HolidayId(3L);
    Country country = Country.of("US", "United States");
    List<String> regions = List.of("California");
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of("Public"), regions);

    // When
    Holiday holiday =
        new Holiday(id, country, "Independence Day", "Independence Day", LocalDate.of(2025, 7, 4), metadata);

    // Then
    assertThat(holiday.getMetadata().applicableRegions()).isUnmodifiable();
  }

  @Test
  @DisplayName("필수 필드가 null이면 예외를 던진다")
  void 필수_필드가_null이면_예외를_던진다() {
    // Given
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of("Public"), null);

    // When & Then
    assertThatThrownBy(() -> new Holiday(null, country, "설날", "Lunar New Year", LocalDate.now(), metadata))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("휴일 ID가 존재하지 않습니다.");

    assertThatThrownBy(
            () ->
                new Holiday(new HolidayId(1L), null, "설날", "Lunar New Year", LocalDate.now(), metadata))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("국가 정보가 존재하지 않습니다.");
  }

  @Test
  @DisplayName("이름이 빈 문자열이면 예외를 던진다")
  void 이름이_빈_문자열이면_예외를_던진다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of("Public"), null);

    // When & Then
    assertThatThrownBy(() -> new Holiday(id, country, "", "Lunar New Year", LocalDate.now(), metadata))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("현지 이름이 존재하지 않습니다.");

    assertThatThrownBy(() -> new Holiday(id, country, "설날", "   ", LocalDate.now(), metadata))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("영문 이름이 존재하지 않습니다.");
  }

  // === 비즈니스 로직 테스트 ===

  @Test
  @DisplayName("특정 날짜가 휴일인지 확인한다")
  void 특정_날짜가_휴일인지_확인한다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("KR", "South Korea");
    LocalDate holidayDate = LocalDate.of(2025, 1, 29);
    Holiday holiday =
        new Holiday(
            id, country, "설날", "Lunar New Year", holidayDate, new HolidayMetadata(true, true, null, List.of(), null));

    // When & Then
    assertThat(holiday.isOn(LocalDate.of(2025, 1, 29))).isTrue();
    assertThat(holiday.isOn(LocalDate.of(2025, 1, 30))).isFalse();
  }

  @Test
  @DisplayName("지역 휴일 여부를 확인한다")
  void 지역_휴일_여부를_확인한다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("US", "United States");
    HolidayMetadata regionalMetadata = new HolidayMetadata(true, false, null, List.of(), List.of("California"));

    Holiday regionalHoliday =
        new Holiday(id, country, "California Day", "California Day", LocalDate.of(2025, 9, 9), regionalMetadata);

    HolidayMetadata nationalMetadata = new HolidayMetadata(true, true, null, List.of(), null);
    Holiday nationalHoliday =
        new Holiday(
            new HolidayId(2L), country, "Independence Day", "Independence Day", LocalDate.of(2025, 7, 4), nationalMetadata);

    // When & Then
    assertThat(regionalHoliday.isRegionalHoliday()).isTrue();
    assertThat(nationalHoliday.isRegionalHoliday()).isFalse();
  }

  @Test
  @DisplayName("특정 지역에 적용되는지 확인한다")
  void 특정_지역에_적용되는지_확인한다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("US", "United States");
    HolidayMetadata metadata = new HolidayMetadata(true, false, null, List.of(), List.of("California", "New York"));
    Holiday regionalHoliday =
        new Holiday(id, country, "California Day", "California Day", LocalDate.of(2025, 9, 9), metadata);

    // When & Then
    assertThat(regionalHoliday.isApplicableTo("California")).isTrue();
    assertThat(regionalHoliday.isApplicableTo("New York")).isTrue();
    assertThat(regionalHoliday.isApplicableTo("Texas")).isFalse();
  }

  @Test
  @DisplayName("전국 적용 휴일은 모든 지역에 적용된다")
  void 전국_적용_휴일은_모든_지역에_적용된다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("US", "United States");
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of(), null);
    Holiday nationalHoliday =
        new Holiday(id, country, "Independence Day", "Independence Day", LocalDate.of(2025, 7, 4), metadata);

    // When & Then
    assertThat(nationalHoliday.isApplicableTo("California")).isTrue();
    assertThat(nationalHoliday.isApplicableTo("Texas")).isTrue();
    assertThat(nationalHoliday.isApplicableTo("New York")).isTrue();
  }

  @Test
  @DisplayName("동일한 국가의 휴일인지 확인한다")
  void 동일한_국가의_휴일인지_확인한다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country krCountry = Country.of("KR", "South Korea");
    Holiday holiday =
        new Holiday(
            id, krCountry, "설날", "Lunar New Year", LocalDate.of(2025, 1, 29), new HolidayMetadata(true, true, null, List.of(), null));

    // When & Then
    assertThat(holiday.isSameCountry(krCountry)).isTrue();
    assertThat(holiday.isSameCountry(Country.of("US", "United States"))).isFalse();
  }

  @Test
  @DisplayName("동일한 ID를 가진 Holiday는 동일하다")
  void 동일한_ID를_가진_Holiday는_동일하다() {
    // Given
    HolidayId id = new HolidayId(1L);
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of(), null);

    Holiday holiday1 =
        new Holiday(id, country, "설날", "Lunar New Year", LocalDate.of(2025, 1, 29), metadata);
    Holiday holiday2 =
        new Holiday(id, country, "다른이름", "Different Name", LocalDate.of(2025, 12, 25), metadata);

    // When & Then
    assertThat(holiday1).isEqualTo(holiday2);
    assertThat(holiday1.hashCode()).isEqualTo(holiday2.hashCode());
  }
}
