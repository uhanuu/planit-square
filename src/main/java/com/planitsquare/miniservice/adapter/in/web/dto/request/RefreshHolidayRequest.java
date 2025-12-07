package com.planitsquare.miniservice.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 휴일 데이터 덮어쓰는 요청 DTO.
 *
 * <p>외부 API로부터 휴일 데이터를 가져와 덮어쓰는 요청을 표현합니다.
 *
 * @param year 조회할 년도
 * @since 1.0
 */
@Schema(description = "휴일 데이터 덮어쓰는 요청")
public record RefreshHolidayRequest(
    @Schema(description = "갱신할 연도", example = "2025")
    @NotNull(message = "갱신할 연도를 입력해주세요.")
    Integer year,
    @Schema(description = "갱신할 국가코드", example = "KR")
    @NotBlank(message = "갱신할 국가코드를 입력해주세요.")
    String countryCode
) {
}
