package com.planitsquare.miniservice.adapter.out.persistence.mapper;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
import com.planitsquare.miniservice.domain.vo.HolidayType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Holiday 도메인 모델과 JPA Entity 간 변환을 담당하는 Mapper.
 *
 * <p>도메인 계층과 영속성 계층의 분리를 위해 변환 로직을 캡슐화합니다.
 *
 * @since 1.0
 */
@Component
public class HolidayEntityMapper {

  /**
   * 도메인 Holiday를 JPA Entity로 변환합니다.
   *
   * @param domain 도메인 Holiday
   * @param countryEntity 국가 JPA Entity
   * @return HolidayJpaEntity
   */
  public HolidayJpaEntity toEntity(Holiday domain, CountryJpaEntity countryEntity) {
    HolidayMetadataEmbeddable metadataEmbeddable = new HolidayMetadataEmbeddable(
        domain.getMetadata().fixed(),
        domain.getMetadata().global(),
        domain.getMetadata().launchYear()
    );

    // String → HolidayType 변환
    List<HolidayType> types = domain.getMetadata().types().stream()
        .map(HolidayType::fromString)
        .collect(Collectors.toList());

    return new HolidayJpaEntity(
        countryEntity,
        domain.getLocalName(),
        domain.getName(),
        domain.getDate(),
        metadataEmbeddable,
        types,
        domain.getMetadata().applicableRegions()
    );
  }

  /**
   * JPA Entity를 도메인 Holiday로 변환합니다.
   *
   * @param entity HolidayJpaEntity
   * @return 도메인 Holiday
   */
  public Holiday toDomain(HolidayJpaEntity entity) {
    Country country = Country.of(
        entity.getCountry().getCode(),
        entity.getCountry().getName()
    );

    // HolidayType → String 변환
    List<String> types = entity.getTypes().stream()
        .map(HolidayType::name)
        .collect(Collectors.toList());

    HolidayMetadata metadata = new HolidayMetadata(
        entity.getMetadata().isFixed(),
        entity.getMetadata().isGlobal(),
        entity.getMetadata().getLaunchYear(),
        types,
        entity.getApplicableRegions()
    );

    return new Holiday(
        new HolidayId(entity.getId()),
        country,
        entity.getLocalName(),
        entity.getName(),
        entity.getDate(),
        metadata
    );
  }
}
