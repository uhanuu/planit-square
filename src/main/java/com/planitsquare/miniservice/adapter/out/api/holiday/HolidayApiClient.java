package com.planitsquare.miniservice.adapter.out.api.holiday;

import com.planitsquare.miniservice.adapter.out.api.holiday.dto.CountryResponse;
import com.planitsquare.miniservice.adapter.out.api.holiday.dto.HolidayResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 외부 공휴일 API 클라이언트.
 *
 * <p>RestClient를 사용하여 외부 공휴일 API와 통신하고,
 * 국가 목록 및 공휴일 정보를 조회합니다.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class HolidayApiClient {

  private final HolidayApiProperties properties;
  private final RestClient restClient;

  /**
   * 사용 가능한 국가 목록을 조회합니다.
   *
   * @return 국가 정보 목록
   */
  public List<CountryResponse> getAvailableCountries() {
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
   * @param countryCode ISO 국가 코드
   * @return 공휴일 정보 목록
   */
  public List<HolidayResponse> getPublicHolidays(int year, String countryCode) {
    String url = properties.getPublicHolidaysUrl(year, countryCode);

    return restClient.get()
        .uri(url)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }
}
