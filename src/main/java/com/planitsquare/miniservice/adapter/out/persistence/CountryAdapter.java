package com.planitsquare.miniservice.adapter.out.persistence;

import com.planitsquare.miniservice.adapter.out.persistence.mapper.CountryMapper;
import com.planitsquare.miniservice.adapter.out.persistence.repository.CountryJpaRepository;
import com.planitsquare.miniservice.application.port.out.FindCountryPort;
import com.planitsquare.miniservice.application.port.out.SaveAllCountriesPort;
import com.planitsquare.miniservice.common.PersistenceAdapter;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 국가 조회 및 저장 Port의 Persistence Adapter 구현체.
 *
 * <p>데이터베이스에서 국가 정보를 조회하고 저장하며, 도메인 모델로 변환합니다.
 *
 * @since 1.0
 */
@PersistenceAdapter
@RequiredArgsConstructor
public class CountryAdapter implements FindCountryPort, SaveAllCountriesPort {
  private final CountryJpaRepository countryRepository;
  private final CountryMapper countryMapper;

  @Override
  public List<Country> findAll() {
    return countryRepository.findAll()
        .stream()
        .map(countryMapper::toDomain)
        .toList();
  }

  @Override
  public void saveAllCountries(List<Country> countries) {
    List<com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity> entities =
        countries.stream()
            .map(countryMapper::toEntity)
            .toList();

    countryRepository.saveAll(entities);
  }
}
