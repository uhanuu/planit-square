package com.planitsquare.miniservice.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HolidayMetadata Value Object 테스트")
class HolidayMetadataTest {

  @Test
  @DisplayName("모든 필드가 정상적으로 생성된다")
  void 모든_필드가_정상적으로_생성된다() {
    // Given
    boolean fixed = true;
    boolean global = false;
    Integer launchYear = 2020;
    List<String> types = List.of("Public", "National");
    List<String> regions = List.of("서울", "부산");

    // When
    HolidayMetadata metadata = new HolidayMetadata(fixed, global, launchYear, types, regions);

    // Then
    assertThat(metadata.fixed()).isTrue();
    assertThat(metadata.global()).isFalse();
    assertThat(metadata.launchYear()).isEqualTo(2020);
    assertThat(metadata.types()).containsExactly("Public", "National");
    assertThat(metadata.applicableRegions()).containsExactly("서울", "부산");
  }

  @Test
  @DisplayName("launchYear가 null이어도 정상적으로 생성된다")
  void launchYear가_null이어도_정상적으로_생성된다() {
    // Given
    boolean fixed = true;
    boolean global = true;
    Integer launchYear = null;
    List<String> types = List.of("Public");

    // When
    HolidayMetadata metadata = new HolidayMetadata(fixed, global, launchYear, types, null);

    // Then
    assertThat(metadata.launchYear()).isNull();
  }

  @Test
  @DisplayName("types가 빈 리스트여도 정상적으로 생성된다")
  void types가_빈_리스트여도_정상적으로_생성된다() {
    // Given
    boolean fixed = false;
    boolean global = false;
    Integer launchYear = 2021;
    List<String> types = Collections.emptyList();

    // When
    HolidayMetadata metadata = new HolidayMetadata(fixed, global, launchYear, types, null);

    // Then
    assertThat(metadata.types()).isEmpty();
  }

  @Test
  @DisplayName("applicableRegions가 null이면 빈 리스트로 초기화된다")
  void applicableRegions가_null이면_빈_리스트로_초기화된다() {
    // Given
    boolean fixed = true;
    boolean global = true;

    // When
    HolidayMetadata metadata = new HolidayMetadata(fixed, global, 2020, List.of("Public"), null);

    // Then
    assertThat(metadata.applicableRegions()).isEmpty();
  }

  @Test
  @DisplayName("types 리스트는 불변이다")
  void types_리스트는_불변이다() {
    // Given
    List<String> types = List.of("Public");
    HolidayMetadata metadata = new HolidayMetadata(true, true, 2020, types, null);

    // When & Then
    assertThat(metadata.types()).isUnmodifiable();
  }

  @Test
  @DisplayName("applicableRegions 리스트는 불변이다")
  void applicableRegions_리스트는_불변이다() {
    // Given
    List<String> regions = List.of("서울");
    HolidayMetadata metadata = new HolidayMetadata(true, true, 2020, List.of("Public"), regions);

    // When & Then
    assertThat(metadata.applicableRegions()).isUnmodifiable();
  }

  @Test
  @DisplayName("같은 값을 가진 HolidayMetadata는 동일하다")
  void 같은_값을_가진_HolidayMetadata는_동일하다() {
    // Given
    HolidayMetadata metadata1 = new HolidayMetadata(true, false, 2020, List.of("Public"), List.of("서울"));
    HolidayMetadata metadata2 = new HolidayMetadata(true, false, 2020, List.of("Public"), List.of("서울"));

    // When & Then
    assertThat(metadata1).isEqualTo(metadata2);
    assertThat(metadata1.hashCode()).isEqualTo(metadata2.hashCode());
  }

  @Test
  @DisplayName("다른 값을 가진 HolidayMetadata는 다르다")
  void 다른_값을_가진_HolidayMetadata는_다르다() {
    // Given
    HolidayMetadata metadata1 = new HolidayMetadata(true, false, 2020, List.of("Public"), null);
    HolidayMetadata metadata2 = new HolidayMetadata(false, false, 2020, List.of("Public"), null);

    // When & Then
    assertThat(metadata1).isNotEqualTo(metadata2);
  }

  @Test
  @DisplayName("지역 휴일 여부를 확인한다")
  void 지역_휴일_여부를_확인한다() {
    // Given
    HolidayMetadata regionalMetadata = new HolidayMetadata(true, false, null, List.of(), List.of("서울"));
    HolidayMetadata nationalMetadata = new HolidayMetadata(true, true, null, List.of(), null);

    // When & Then
    assertThat(regionalMetadata.isRegionalHoliday()).isTrue();
    assertThat(nationalMetadata.isRegionalHoliday()).isFalse();
  }

  @Test
  @DisplayName("특정 지역에 적용되는지 확인한다")
  void 특정_지역에_적용되는지_확인한다() {
    // Given
    HolidayMetadata metadata = new HolidayMetadata(true, false, null, List.of(), List.of("서울", "부산"));

    // When & Then
    assertThat(metadata.isApplicableTo("서울")).isTrue();
    assertThat(metadata.isApplicableTo("부산")).isTrue();
    assertThat(metadata.isApplicableTo("대구")).isFalse();
  }

  @Test
  @DisplayName("전국 적용 휴일은 모든 지역에 적용된다")
  void 전국_적용_휴일은_모든_지역에_적용된다() {
    // Given
    HolidayMetadata metadata = new HolidayMetadata(true, true, null, List.of(), null);

    // When & Then
    assertThat(metadata.isApplicableTo("서울")).isTrue();
    assertThat(metadata.isApplicableTo("부산")).isTrue();
    assertThat(metadata.isApplicableTo("대구")).isTrue();
  }
}
