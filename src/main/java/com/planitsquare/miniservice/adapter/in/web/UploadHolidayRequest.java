package com.planitsquare.miniservice.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 휴일 데이터 업로드 요청 DTO.
 *
 * <p>외부 API로부터 휴일 데이터를 가져와 저장하는 요청을 표현합니다.
 *
 * @param year          조회할 년도 (2020 ~ 2030)
 * @param executionType 실행 타입 (INITIAL_SYSTEM_LOAD, SCHEDULED_BATCH, API_REFRESH, MANUAL_EXECUTION, EVENT_TRIGGERED)
 * @since 1.0
 */
@Schema(description = "휴일 데이터 업로드 요청")
public record UploadHolidayRequest(
    @Schema(description = "조회할 년도", example = "2025", minimum = "2020", maximum = "2025")
    @Min(value = 2020, message = "Year must be at least 2020")
    @Max(value = 2025, message = "Year must be at most 2025")
    int year,

    @Schema(
        description = "실행 타입",
        example = "API_REFRESH",
        allowableValues = {
            "INITIAL_SYSTEM_LOAD",
            "SCHEDULED_BATCH",
            "API_REFRESH",
            "MANUAL_EXECUTION",
            "EVENT_TRIGGERED"
        }
    )
    @NotBlank(message = "Execution type must not be blank")
    String executionType
) {
}
