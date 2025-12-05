package com.planitsquare.miniservice.adapter.out.api.holiday;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 외부 공휴일 API 설정 속성.
 *
 * <p>application.yml의 {@code external-api} 하위 속성과 바인딩됩니다.
 * 공휴일 관련 API 호출에 필요한 기본 URL과 엔드포인트 경로를 제공합니다.
 *
 * @since 1.0
 */
@Getter
@ConfigurationProperties(prefix = "external-api")
public class HolidayApiProperties {

    private final String baseUrl;
    private final Endpoints endpoints;

    /**
     * 생성자 기반 속성 바인딩.
     *
     * @param baseUrl   기본 URL
     * @param endpoints 엔드포인트 설정
     */
    public HolidayApiProperties(String baseUrl, Endpoints endpoints) {
        this.baseUrl = baseUrl;
        this.endpoints = endpoints;
    }

    /**
     * 공휴일 API 엔드포인트 경로를 담는 내부 클래스.
     *
     * @param availableCountries 국가 목록 엔드포인트 경로
     * @param publicHolidays     공휴일 엔드포인트 경로
     */
    public record Endpoints(
            String availableCountries,
            String publicHolidays
    ) {
    }

    /**
     * 사용 가능한 국가 목록 조회를 위한 전체 URL을 생성합니다.
     *
     * @return 기본 URL과 엔드포인트 경로를 결합한 전체 URL
     */
    public String getAvailableCountriesUrl() {
        return baseUrl + endpoints.availableCountries;
    }

    /**
     * 특정 연도와 국가의 공휴일 조회를 위한 전체 URL을 생성합니다.
     *
     * @param year        조회할 연도
     * @param countryCode ISO 국가 코드
     * @return 연도와 국가 코드가 포함된 전체 URL
     */
    public String getPublicHolidaysUrl(int year, String countryCode) {
        return baseUrl + endpoints.publicHolidays
                .replace("{year}", String.valueOf(year))
                .replace("{countryCode}", countryCode);
    }
}
