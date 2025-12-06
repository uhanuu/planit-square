package com.planitsquare.miniservice.domain.vo;

import lombok.Getter;

import java.util.Arrays;

/**
 * 휴일 유형을 정의하는 열거형.
 *
 * <p>외부 API로부터 제공되는 휴일 타입을 나타냅니다.
 *
 * @since 1.0
 */
@Getter
public enum HolidayType {
    PUBLIC("Public", "공휴일"),
    BANK("Bank", "은행 휴무일"),
    SCHOOL("School", "학교 휴일"),
    AUTHORITIES("Authorities", "관공서 휴일"),
    OPTIONAL("Optional", "선택적 휴일"),
    OBSERVANCE("Observance", "기념일");

    private final String name;
    private final String description;

    HolidayType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 문자열로부터 HolidayType을 조회합니다.
     *
     * @param type 휴일 유형 문자열 (예: "Public", "Bank")
     * @return 매칭되는 HolidayType
     * @throws IllegalArgumentException 매칭되는 유형이 없는 경우
     */
    public static HolidayType fromString(String type) {
        return Arrays.stream(values())
                .filter(t -> t.name.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(type + "는 잘못된 공휴일 유형입니다."));
    }
}
