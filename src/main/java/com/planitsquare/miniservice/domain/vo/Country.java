package com.planitsquare.miniservice.domain.vo;

/**
 * 국가 정보를 나타내는 Value Object.
 *
 * <p>국가 코드와 이름을 캡슐화하는 불변 객체입니다.
 * 동일한 국가 코드를 가진 국가는 동일한 것으로 간주됩니다.
 *
 * @param code 국가 코드 (대문자로 정규화)
 * @param name 국가 이름
 * @since 1.0
 */
public record Country(CountryCode code, String name) {

  /**
   * Country를 생성합니다.
   *
   * @param code 국가 코드
   * @param name 국가 이름
   * @throws IllegalArgumentException 국가 이름이 null이거나 빈 문자열인 경우
   */
  public Country {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("국가 이름이 존재하지 않습니다.");
    }
  }

  /**
   * 문자열 코드로 Country를 생성하는 정적 팩토리 메서드.
   *
   * @param code 국가 코드 문자열
   * @param name 국가 이름
   * @return 생성된 Country 객체
   */
  public static Country of(String code, String name) {
    return new Country(new CountryCode(code), name);
  }

  /**
   * 국가 코드를 문자열로 반환합니다.
   *
   * @return 국가 코드 문자열
   */
  public String getCode() {
    return code.code();
  }

  /**
   * 국가 이름을 반환합니다.
   *
   * @return 국가 이름
   */
  public String getName() {
    return name;
  }

    /**
     * 동일한 국가 코드의 휴일인지 확인합니다.
     *
     * @param countryCode 확인할 국가 코드
     * @return 동일한 국가 코드이면 true
     */
    public boolean isSameCountryCode(CountryCode countryCode) {
        return code.equals(countryCode);
    }
}
