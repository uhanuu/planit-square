package com.planitsquare.miniservice.domain.model;

import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Country Value Object 테스트")
public class CountryTest {

    @Test
    @DisplayName("국가 이름은 null일 수 없다")
    public void 국가_정보는_null_일_수_없다() {
        // Given
        String code = "US";
        String name = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Country.of(code, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("국가 이름이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("국가 이름은 빈 문자열일 수 없다")
    public void 국가_이름은_빈_문자열일_수_없다() {
        // Given
        String code = "US";
        String name = "   ";

        // When & Then
        Assertions.assertThatThrownBy(() -> Country.of(code, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("국가 이름이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("동일한 국가 코드를 가진 Country는 동일하다")
    public void 동일한_국가_코드를_가진_Country는_동일하다() {
        // Given
        Country country1 = Country.of("KR", "South Korea");
        Country country2 = Country.of("kr", "대한민국");

        // When & Then
        Assertions.assertThat(country1.code()).isEqualTo(country2.code());
    }

    @Test
    @DisplayName("Country는 불변 객체다")
    public void Country는_불변_객체다() {
        // Given & When
        Country country = Country.of("US", "United States");

        // Then
        Assertions.assertThat(country.getCode()).isEqualTo("US");
        Assertions.assertThat(country.getName()).isEqualTo("United States");
    }

    @Test
    @DisplayName("동일한 국가 코드인지 확인한다")
    public void 동일한_국가_코드인지_확인한다() {
        // Given
        Country country = Country.of("KR", "South Korea");
        CountryCode sameCode = new CountryCode("KR");
        CountryCode differentCode = new CountryCode("US");

        // When & Then
        Assertions.assertThat(country.isSameCountryCode(sameCode)).isTrue();
        Assertions.assertThat(country.isSameCountryCode(differentCode)).isFalse();
    }
}
