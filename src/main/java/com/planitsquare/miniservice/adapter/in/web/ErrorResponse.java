package com.planitsquare.miniservice.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * API 에러 응답 DTO.
 *
 * <p>클라이언트에게 반환되는 표준화된 에러 응답 형식을 정의합니다.
 *
 * @param timestamp 에러 발생 시각
 * @param status HTTP 상태 코드
 * @param error HTTP 상태 텍스트
 * @param message 에러 메시지
 * @param path 요청 경로
 * @since 1.0
 */
@Schema(description = "API 에러 응답")
public record ErrorResponse(
    @Schema(description = "에러 발생 시각", example = "2025-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,

    @Schema(description = "HTTP 상태 코드", example = "400")
    int status,

    @Schema(description = "HTTP 상태 텍스트", example = "Bad Request")
    String error,

    @Schema(description = "에러 메시지", example = "유효하지 않은 요청입니다.")
    String message,

    @Schema(description = "요청 경로", example = "/api/v1/holidays")
    String path
) {
  /**
   * ErrorResponse 생성자.
   *
   * @param status HTTP 상태 코드
   * @param error HTTP 상태 텍스트
   * @param message 에러 메시지
   * @param path 요청 경로
   * @return ErrorResponse 인스턴스
   * @since 1.0
   */
  public static ErrorResponse of(int status, String error, String message, String path) {
    return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
  }
}
