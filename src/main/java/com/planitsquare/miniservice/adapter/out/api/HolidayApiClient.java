package com.planitsquare.miniservice.adapter.out.api;

import com.planitsquare.miniservice.adapter.out.api.dto.CountryResponse;
import com.planitsquare.miniservice.adapter.out.api.dto.HolidayResponse;
import com.planitsquare.miniservice.adapter.out.api.mapper.CountryResponseMapper;
import com.planitsquare.miniservice.adapter.out.api.mapper.HolidayResponseMapper;
import com.planitsquare.miniservice.application.exception.ExternalApiException;
import com.planitsquare.miniservice.application.port.out.FetchCountriesPort;
import com.planitsquare.miniservice.application.port.out.FetchHolidaysPort;
import com.planitsquare.miniservice.common.ExternalApiAdapter;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
  @Retry(name = "holidayApi", fallbackMethod = "fetchCountriesFallback")
  public List<Country> fetchCountries() {
    return getAvailableCountries()
        .stream()
        .map(countryResponseMapper::toDomain)
        .toList();
  }

  /**
   * fetchCountries 실패 시 fallback 메서드.
   *
   * @param ex 발생한 예외
   * @return 예외를 던짐
   * @throws ExternalApiException 외부 API 호출 실패 예외
   */
  private List<Country> fetchCountriesFallback(Exception ex) {
    log.error("국가 목록 조회 실패 (모든 재시도 실패) - 예외: {}", ex.getMessage(), ex);

    throw new ExternalApiException("외부 API에서 국가 목록을 조회할 수 없습니다.", ex);
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
  @Retry(name = "holidayApi", fallbackMethod = "fetchHolidaysFallback")
  public List<Holiday> fetchHolidays(int year, Country country) {
    return getPublicHolidays(year, country.getCode())
        .stream()
        .map(response -> holidayResponseMapper.toDomain(response, country))
        .toList();
  }

  /**
   * fetchHolidays 실패 시 fallback 메서드.
   *
   * @param year 연도
   * @param country 국가
   * @param ex 발생한 예외
   * @return 예외를 던짐
   * @throws ExternalApiException 외부 API 호출 실패 예외
   */
  private List<Holiday> fetchHolidaysFallback(int year, Country country, Exception ex) {
    log.error("공휴일 조회 실패 (모든 재시도 실패) - 국가: {}, 연도: {}, 예외: {}",
        country.getCode(), year, ex.getMessage(), ex);

    throw new ExternalApiException(
        String.format("외부 API에서 공휴일을 조회할 수 없습니다. (국가: %s, 연도: %d)",
            country.getCode(), year),
        ex);
  }

  private List<HolidayResponse> getPublicHolidays(int year, String countryCode) {
    String url = properties.getPublicHolidaysUrl(year, countryCode);

    return restClient.get()
        .uri(url)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }
}
