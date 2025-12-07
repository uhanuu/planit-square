package com.planitsquare.miniservice.application.port.in;

/**
 * 공휴일 덮어쓰기 응답 DTO.
 *
 * <p>특정 연도와 국가의 공휴일 덮어쓰기 건수를 표현합니다.
 *
 * @param deleteCount 삭제된 공휴일 건수
 * @param insertCount 추가된 공휴일 건수
 * @since 1.0
 */
public record RefreshHolidayDto(
    int deleteCount,
    int insertCount
) {
}
