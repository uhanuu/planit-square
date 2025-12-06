package com.planitsquare.miniservice.adapter.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
import com.planitsquare.miniservice.domain.vo.HolidayType;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HolidayEntityMapper 테스트")
class HolidayEntityMapperTest {

  private HolidayEntityMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new HolidayEntityMapper();
  }

  /**
   * 테스트용: Reflection을 사용하여 JPA Entity의 ID를 설정합니다.
   */
  private void setEntityId(HolidayJpaEntity entity, Long id) throws Exception {
    Field idField = HolidayJpaEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(entity, id);
  }

  @Test
  @DisplayName("도메인 Holiday를 JPA Entity로 변환한다")
  void 도메인_Holiday를_JPA_Entity로_변환한다() {
    // Given
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(
        true, true, 1949,
        List.of("PUBLIC"),
        List.of("서울", "부산")
    );
    Holiday domain = new Holiday(
        new HolidayId(1L),
        country,
        "설날",
        "Lunar New Year",
        LocalDate.of(2025, 1, 29),
        metadata
    );
    CountryJpaEntity countryEntity = new CountryJpaEntity("KR", "South Korea");

    // When
    HolidayJpaEntity entity = mapper.toEntity(domain, countryEntity);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getCountry()).isEqualTo(countryEntity);
    assertThat(entity.getLocalName()).isEqualTo("설날");
    assertThat(entity.getName()).isEqualTo("Lunar New Year");
    assertThat(entity.getDate()).isEqualTo(LocalDate.of(2025, 1, 29));
    assertThat(entity.getMetadata().isFixed()).isTrue();
    assertThat(entity.getMetadata().isGlobal()).isTrue();
    assertThat(entity.getMetadata().getLaunchYear()).isEqualTo(1949);
    assertThat(entity.getTypes()).containsExactly(HolidayType.PUBLIC);
    assertThat(entity.getApplicableRegions()).containsExactly("서울", "부산");
  }

  @Test
  @DisplayName("JPA Entity를 도메인 Holiday로 변환한다")
  void JPA_Entity를_도메인_Holiday로_변환한다() throws Exception {
    // Given
    CountryJpaEntity countryEntity = new CountryJpaEntity("US", "United States");
    HolidayMetadataEmbeddable metadata =
        new HolidayMetadataEmbeddable(
            true, false, 2020
        );
    HolidayJpaEntity entity = new HolidayJpaEntity(
        countryEntity,
        "Independence Day",
        "Independence Day",
        LocalDate.of(2025, 7, 4),
        metadata,
        List.of(HolidayType.PUBLIC),
        List.of("California", "New York")
    );
    setEntityId(entity, 1L);

    // When
    Holiday domain = mapper.toDomain(entity);

    // Then
    assertThat(domain).isNotNull();
    assertThat(domain.getCountry().getCode()).isEqualTo("US");
    assertThat(domain.getCountry().getName()).isEqualTo("United States");
    assertThat(domain.getLocalName()).isEqualTo("Independence Day");
    assertThat(domain.getName()).isEqualTo("Independence Day");
    assertThat(domain.getDate()).isEqualTo(LocalDate.of(2025, 7, 4));
    assertThat(domain.getMetadata().fixed()).isTrue();
    assertThat(domain.getMetadata().global()).isFalse();
    assertThat(domain.getMetadata().launchYear()).isEqualTo(2020);
    assertThat(domain.getMetadata().types()).containsExactly("PUBLIC");
    assertThat(domain.getMetadata().applicableRegions()).containsExactly("California", "New York");
  }

  @Test
  @DisplayName("HolidayType String과 Enum 간 변환이 올바르게 수행된다")
  void HolidayType_String과_Enum_간_변환이_올바르게_수행된다() throws Exception {
    // Given
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(
        true, true, null,
        List.of("PUBLIC"),
        List.of()
    );
    Holiday domain = new Holiday(
        new HolidayId(1L),
        country,
        "설날",
        "Lunar New Year",
        LocalDate.now(),
        metadata
    );
    CountryJpaEntity countryEntity = new CountryJpaEntity("KR", "South Korea");

    // When: Domain → Entity 변환
    HolidayJpaEntity entity = mapper.toEntity(domain, countryEntity);

    // Then: String "PUBLIC" → Enum HolidayType.PUBLIC
    assertThat(entity.getTypes()).containsExactly(HolidayType.PUBLIC);

    // When: Entity → Domain 변환 (ID 설정 필요)
    setEntityId(entity, 1L);
    Holiday convertedDomain = mapper.toDomain(entity);

    // Then: Enum HolidayType.PUBLIC → String "PUBLIC"
    assertThat(convertedDomain.getMetadata().types()).containsExactly("PUBLIC");
  }

  @Test
  @DisplayName("빈 리스트도 올바르게 변환된다")
  void 빈_리스트도_올바르게_변환된다() {
    // Given
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(
        false, false, null,
        List.of(),
        List.of()
    );
    Holiday domain = new Holiday(
        new HolidayId(1L),
        country,
        "설날",
        "Lunar New Year",
        LocalDate.now(),
        metadata
    );
    CountryJpaEntity countryEntity = new CountryJpaEntity("KR", "South Korea");

    // When
    HolidayJpaEntity entity = mapper.toEntity(domain, countryEntity);

    // Then
    assertThat(entity.getTypes()).isEmpty();
    assertThat(entity.getApplicableRegions()).isEmpty();
  }

  @Test
  @DisplayName("도메인과 Entity 간 양방향 변환이 일관성을 유지한다")
  void 도메인과_Entity_간_양방향_변환이_일관성을_유지한다() throws Exception {
    // Given
    Country country = Country.of("KR", "South Korea");
    HolidayMetadata metadata = new HolidayMetadata(
        true, true, 1949,
        List.of("PUBLIC"),
        List.of("서울", "부산")
    );
    Holiday originalDomain = new Holiday(
        new HolidayId(1L),
        country,
        "설날",
        "Lunar New Year",
        LocalDate.of(2025, 1, 29),
        metadata
    );
    CountryJpaEntity countryEntity = new CountryJpaEntity("KR", "South Korea");

    // When: Domain → Entity → Domain 변환 (ID 설정 필요)
    HolidayJpaEntity entity = mapper.toEntity(originalDomain, countryEntity);
    setEntityId(entity, 1L);
    Holiday convertedDomain = mapper.toDomain(entity);

    // Then: 원본 도메인과 변환된 도메인의 데이터가 일치
    assertThat(convertedDomain.getCountry().getCode()).isEqualTo(originalDomain.getCountry().getCode());
    assertThat(convertedDomain.getLocalName()).isEqualTo(originalDomain.getLocalName());
    assertThat(convertedDomain.getName()).isEqualTo(originalDomain.getName());
    assertThat(convertedDomain.getDate()).isEqualTo(originalDomain.getDate());
    assertThat(convertedDomain.getMetadata().fixed()).isEqualTo(originalDomain.getMetadata().fixed());
    assertThat(convertedDomain.getMetadata().global()).isEqualTo(originalDomain.getMetadata().global());
    assertThat(convertedDomain.getMetadata().launchYear()).isEqualTo(originalDomain.getMetadata().launchYear());
    assertThat(convertedDomain.getMetadata().types()).isEqualTo(originalDomain.getMetadata().types());
    assertThat(convertedDomain.getMetadata().applicableRegions())
        .isEqualTo(originalDomain.getMetadata().applicableRegions());
  }
}
