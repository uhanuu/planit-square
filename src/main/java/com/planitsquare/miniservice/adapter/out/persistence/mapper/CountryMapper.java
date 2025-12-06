package com.planitsquare.miniservice.adapter.out.persistence.mapper;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.domain.vo.Country;
import org.springframework.stereotype.Component;

/**
 * Country 도메인 모델과 CountryJpaEntity 간의 변환을 담당하는 Mapper.
 *
 * <p>도메인 계층과 영속성 계층 간의 변환을 수행합니다.
 *
 * @since 1.0
 */
@Component
public class CountryMapper {

  /**
   * CountryJpaEntity를 Country 도메인 모델로 변환합니다.
   *
   * @param entity CountryJpaEntity
   * @return Country 도메인 모델
   */
  public Country toDomain(CountryJpaEntity entity) {
    return Country.of(entity.getCode(), entity.getName());
  }

  /**
   * Country 도메인 모델을 CountryJpaEntity로 변환합니다.
   *
   * @param domain Country 도메인 모델
   * @return CountryJpaEntity
   */
  public CountryJpaEntity toEntity(Country domain) {
    return new CountryJpaEntity(domain.getCode(), domain.getName());
  }
}
