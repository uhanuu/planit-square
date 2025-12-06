package com.planitsquare.miniservice.domain.vo;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum HolidayType {
    PUBLIC("Public");

    private final String name;

    HolidayType(String name) {
        this.name = name;
    }

    public static HolidayType fromString(String type) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(type + "는 잘못된 공휴일 유형입니다."));
    }
}
