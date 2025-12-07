package com.planitsquare.miniservice.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공휴일 덮어쓰기 응답 DTO.
 *
 * <p>특정 연도와 국가의 공휴일 덮어쓰기 건수를 표현합니다.
 *
 * @param deleteCount 삭제된 공휴일 건수
 * @param insertCount 추가된 공휴일 건수
 * @since 1.0
 */
@Schema(description = "공휴일 덮어쓰기 응답")
public record RefreshHolidayResponse(
    @Schema(description = "삭제된 공휴일 건수", example = "10")
    int deleteCount,
    @Schema(description = "추가된 공휴일 건수", example = "10")
    int insertCount
) {
}
