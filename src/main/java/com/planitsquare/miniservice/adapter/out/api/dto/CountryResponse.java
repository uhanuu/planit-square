package com.planitsquare.miniservice.adapter.out.api.dto;

import lombok.Getter;

/**
 * 사용 가능한 국가 정보 응답 DTO.
 *
 * <p>외부 API로부터 받은 국가 정보를 담는 데이터 전송 객체입니다.
 *
 * @since 1.0
 */
@Getter
public class CountryResponse {

  /**
   * ISO 3166-1 alpha-2 국가 코드.
   *
   * <p>예시: {@code KR}, {@code US}, {@code JP}
   */
  private String countryCode;

  /**
   * 국가명 (영문).
   *
   * <p>예시: {@code South Korea}, {@code United States}, {@code Japan}
   */
  private String name;
}
