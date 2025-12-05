package com.planitsquare.miniservice.domain.vo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountryCodeTest {

    @Test
    public void 국가_코드는_null_일_수_없다() {
        // given
        String code = null;

        // when then
        Assertions.assertThatThrownBy(() -> new CountryCode(code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("국가 코드가 존재하지 않습니다.");
    }

    @Test
    public void 국가_코드는_빈문자열_일_수_없다() {
        //given
        String code = "";

        // when then
        Assertions.assertThatThrownBy(() -> new CountryCode(code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("국가 코드가 존재하지 않습니다.");
    }

    @Test
    public void 국가_코드는_대문자로_변경된다() {
        //given
        String code = "kr";
        //when
        CountryCode countryCode = new CountryCode(code);
        String result = countryCode.code();
        //then
        Assertions.assertThat(result).isEqualTo(code.toUpperCase());
    }

}