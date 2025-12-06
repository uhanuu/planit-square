package com.planitsquare.miniservice.adapter.out.api;

import com.planitsquare.miniservice.adapter.out.api.dto.CountryResponse;
import com.planitsquare.miniservice.adapter.out.api.dto.HolidayResponse;
import com.planitsquare.miniservice.adapter.out.api.mapper.CountryResponseMapper;
import com.planitsquare.miniservice.adapter.out.api.mapper.HolidayResponseMapper;
import java.util.List;

import com.planitsquare.miniservice.application.port.out.FetchCountriesPort;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.common.ExternalApiAdapter;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

/**
 * 외부 공휴일 API 클라이언트.
 *
 * <p>RestClient를 사용하여 외부 공휴일 API와 통신하고,
 * 국가 목록 및 공휴일 정보를 조회합니다.
 *
 * @since 1.0
 */
@ExternalApiAdapter
@RequiredArgsConstructor
public class HolidayApiClient implements FetchHolidaysPort, FetchCountriesPort {

  private final HolidayApiProperties properties;
  private final RestClient restClient;
  private final CountryResponseMapper countryResponseMapper;
  private final HolidayResponseMapper holidayResponseMapper;

  /**
   * 사용 가능한 국가 목록을 조회합니다.
   *
   * @return 국가 정보 목록
   */
  @Override
  public List<Country> fetchCountries() {
    return getAvailableCountries()
        .stream()
        .map(countryResponseMapper::toDomain)
        .toList();
  }

  private List<CountryResponse> getAvailableCountries() {
    String url = properties.getAvailableCountriesUrl();

    return restClient.get()
        .uri(url)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }

  /**
   * 특정 연도와 국가의 공휴일 목록을 조회합니다.
   *
   * @param year 조회할 연도
   * @param country ISO 국가 정보
   * @return 공휴일 정보 목록
   */
  @Override
  public List<Holiday> fetchHolidays(int year, Country country) {
    return getPublicHolidays(year, country.getCode())
        .stream()
        .map(response -> holidayResponseMapper.toDomain(response, country))
        .toList();
  }

  private List<HolidayResponse> getPublicHolidays(int year, String countryCode) {
    String url = properties.getPublicHolidaysUrl(year, countryCode);

    return restClient.get()
        .uri(url)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }
}
