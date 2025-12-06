package com.planitsquare.miniservice.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import com.planitsquare.miniservice.domain.vo.HolidayType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HolidayJpaEntity 테스트")
class HolidayJpaEntityTest {

  @Test
  @DisplayName("HolidayJpaEntity가 정상적으로 생성된다")
  void HolidayJpaEntity가_정상적으로_생성된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    String localName = "설날";
    String name = "Lunar New Year";
    LocalDate date = LocalDate.of(2025, 1, 29);
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, true, 1949);
    List<HolidayType> types = List.of(HolidayType.PUBLIC);
    List<String> applicableRegions = List.of("서울", "부산");

    // When
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, localName, name, date, metadata, types, applicableRegions
    );

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getCountry()).isEqualTo(country);
    assertThat(entity.getLocalName()).isEqualTo("설날");
    assertThat(entity.getName()).isEqualTo("Lunar New Year");
    assertThat(entity.getDate()).isEqualTo(date);
    assertThat(entity.getMetadata()).isEqualTo(metadata);
    assertThat(entity.getTypes()).containsExactly(HolidayType.PUBLIC);
    assertThat(entity.getApplicableRegions()).containsExactly("서울", "부산");
  }

  @Test
  @DisplayName("types가 null이면 빈 리스트로 초기화된다")
  void types가_null이면_빈_리스트로_초기화된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, true, null);

    // When
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, "설날", "Lunar New Year", LocalDate.now(), metadata, null, null
    );

    // Then
    assertThat(entity.getTypes()).isEmpty();
    assertThat(entity.getApplicableRegions()).isEmpty();
  }

  @Test
  @DisplayName("휴일 정보를 업데이트한다")
  void 휴일_정보를_업데이트한다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, true, 1949);
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, "설날", "Lunar New Year", LocalDate.of(2025, 1, 29),
        metadata, List.of(HolidayType.PUBLIC), List.of("서울")
    );

    // When
    String newLocalName = "추석";
    String newName = "Harvest Moon";
    LocalDate newDate = LocalDate.of(2025, 10, 6);
    HolidayMetadataEmbeddable newMetadata = new HolidayMetadataEmbeddable(true, true, 1950);
    List<HolidayType> newTypes = List.of(HolidayType.PUBLIC);
    List<String> newRegions = List.of("서울", "부산", "대구");

    entity.update(newLocalName, newName, newDate, newMetadata, newTypes, newRegions);

    // Then
    assertThat(entity.getLocalName()).isEqualTo("추석");
    assertThat(entity.getName()).isEqualTo("Harvest Moon");
    assertThat(entity.getDate()).isEqualTo(newDate);
    assertThat(entity.getMetadata()).isEqualTo(newMetadata);
    assertThat(entity.getTypes()).containsExactly(HolidayType.PUBLIC);
    assertThat(entity.getApplicableRegions()).containsExactly("서울", "부산", "대구");
  }

  @Test
  @DisplayName("업데이트 시 types와 applicableRegions가 null이면 빈 리스트로 초기화된다")
  void 업데이트_시_types와_applicableRegions가_null이면_빈_리스트로_초기화된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, true, 1949);
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, "설날", "Lunar New Year", LocalDate.now(),
        metadata, List.of(HolidayType.PUBLIC), List.of("서울")
    );

    // When
    entity.update("추석", "Harvest Moon", LocalDate.now(), metadata, null, null);

    // Then
    assertThat(entity.getTypes()).isEmpty();
    assertThat(entity.getApplicableRegions()).isEmpty();
  }

  @Test
  @DisplayName("Metadata가 올바르게 임베드된다")
  void Metadata가_올바르게_임베드된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("US", "United States");
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, false, 2020);

    // When
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, "Independence Day", "Independence Day",
        LocalDate.of(2025, 7, 4), metadata, List.of(), List.of()
    );

    // Then
    assertThat(entity.getMetadata().isFixed()).isTrue();
    assertThat(entity.getMetadata().isGlobal()).isFalse();
    assertThat(entity.getMetadata().getLaunchYear()).isEqualTo(2020);
  }

  @Test
  @DisplayName("Country와 ManyToOne 관계가 유지된다")
  void Country와_ManyToOne_관계가_유지된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    HolidayMetadataEmbeddable metadata = new HolidayMetadataEmbeddable(true, true, null);

    // When
    HolidayJpaEntity entity = new HolidayJpaEntity(
        country, "설날", "Lunar New Year", LocalDate.now(),
        metadata, List.of(), List.of()
    );

    // Then
    assertThat(entity.getCountry()).isSameAs(country);
    assertThat(entity.getCountry().getCode()).isEqualTo("KR");
    assertThat(entity.getCountry().getName()).isEqualTo("South Korea");
  }
}
