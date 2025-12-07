package com.planitsquare.miniservice.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 휴일 데이터 업로드 요청 DTO.
 *
 * <p>외부 API로부터 휴일 데이터를 가져와 저장하는 요청을 표현합니다.
 *
 * @param year 조회할 년도
 * @since 1.0
 */
@Schema(description = "휴일 데이터 업로드 요청")
public record UploadHolidayRequest(
    @Schema(description = "조회할 년도", example = "2025")
    int year
) {
}
