package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.vo.Country;

import java.util.List;

/**
 * 외부 API로부터 국가 정보를 조회하는 Port.
 *
 * <p>외부 시스템으로부터 사용 가능한 국가 목록을 가져옵니다.
 *
 * @since 1.0
 */
public interface FetchCountriesPort {
  /**
   * 외부 API로부터 사용 가능한 국가 목록을 조회합니다.
   *
   * @return 국가 정보 목록
   */
  List<Country> fetchCountries();
}
