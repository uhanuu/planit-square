package com.planitsquare.miniservice.adapter.out.api.mapper;

import com.planitsquare.miniservice.adapter.out.api.dto.CountryResponse;
import com.planitsquare.miniservice.domain.vo.Country;
import org.springframework.stereotype.Component;

/**
 * CountryResponse를 Country 도메인 모델로 변환하는 Mapper.
 *
 * <p>외부 API 응답 DTO를 도메인 계층의 Value Object로 변환합니다.
 *
 * @since 1.0
 */
@Component
public class CountryResponseMapper {

  /**
   * CountryResponse를 Country 도메인 모델로 변환합니다.
   *
   * @param response 외부 API 응답 DTO
   * @return Country 도메인 모델
   * @throws IllegalArgumentException 국가 코드나 이름이 null이거나 빈 문자열인 경우
   */
  public Country toDomain(CountryResponse response) {
    return Country.of(response.getCountryCode(), response.getName());
  }
}
