package com.planitsquare.miniservice.domain.vo;

public record CountryCode(String code) {
    public CountryCode {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("국가 코드가 존재하지 않습니다.");
        }
        code = code.toUpperCase();
    }
}
