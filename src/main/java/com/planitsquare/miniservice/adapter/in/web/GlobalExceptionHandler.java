package com.planitsquare.miniservice.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리기.
 *
 * <p>애플리케이션 전역에서 발생하는 예외를 처리하고 표준화된 에러 응답을 반환합니다.
 *
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 유효성 검증 실패 예외를 처리합니다.
   *
   * @param ex 유효성 검증 예외
   * @param request HTTP 요청
   * @return 에러 응답
   * @since 1.0
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));

    log.warn("유효성 검증 실패 - 경로: {}, 메시지: {}", request.getRequestURI(), errorMessage);

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        errorMessage,
        request.getRequestURI()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * IllegalArgumentException을 처리합니다.
   *
   * @param ex IllegalArgumentException
   * @param request HTTP 요청
   * @return 에러 응답
   * @since 1.0
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      HttpServletRequest request
  ) {
    log.warn("잘못된 인자 - 경로: {}, 메시지: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * IllegalStateException을 처리합니다.
   *
   * @param ex IllegalStateException
   * @param request HTTP 요청
   * @return 에러 응답
   * @since 1.0
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      IllegalStateException ex,
      HttpServletRequest request
  ) {
    log.warn("잘못된 상태 - 경로: {}, 메시지: {}", request.getRequestURI(), ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.CONFLICT.value(),
        HttpStatus.CONFLICT.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  /**
   * 그 외 모든 예외를 처리합니다.
   *
   * @param ex 예외
   * @param request HTTP 요청
   * @return 에러 응답
   * @since 1.0
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(
      Exception ex,
      HttpServletRequest request
  ) {
    log.error("서버 내부 오류 - 경로: {}, 메시지: {}", request.getRequestURI(), ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        "서버 내부 오류가 발생했습니다.",
        request.getRequestURI()
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }
}
