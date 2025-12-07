package com.planitsquare.miniservice.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공휴일 삭제 응답 DTO.
 *
 * <p>특정 연도와 국가의 공휴일 삭제 결과를 표현합니다.
 *
 * @param deletedCount 삭제된 공휴일 건수
 * @since 1.0
 */
@Schema(description = "공휴일 삭제 응답")
public record DeleteHolidayResponse(
    @Schema(description = "삭제된 공휴일 건수", example = "10")
    int deletedCount
) {
}
