package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/test");
  }

  @Test
  @DisplayName("유효성 검증 실패 시 400 에러를 반환한다")
  void 유효성_검증_실패시_400_에러를_반환한다() {
    // Given
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "must not be null");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    // When
    ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(400);
    assertThat(response.getBody().error()).isEqualTo("Bad Request");
    assertThat(response.getBody().message()).contains("field");
    assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
  }

  @Test
  @DisplayName("IllegalArgumentException 발생 시 400 에러를 반환한다")
  void IllegalArgumentException_발생시_400_에러를_반환한다() {
    // Given
    IllegalArgumentException ex = new IllegalArgumentException("잘못된 인자입니다");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(400);
    assertThat(response.getBody().message()).isEqualTo("잘못된 인자입니다");
    assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
  }

  @Test
  @DisplayName("IllegalStateException 발생 시 409 에러를 반환한다")
  void IllegalStateException_발생시_409_에러를_반환한다() {
    // Given
    IllegalStateException ex = new IllegalStateException("잘못된 상태입니다");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(409);
    assertThat(response.getBody().message()).isEqualTo("잘못된 상태입니다");
    assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
  }

  @Test
  @DisplayName("일반 예외 발생 시 500 에러를 반환한다")
  void 일반_예외_발생시_500_에러를_반환한다() {
    // Given
    Exception ex = new RuntimeException("예상치 못한 오류");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(500);
    assertThat(response.getBody().message()).isEqualTo("서버 내부 오류가 발생했습니다.");
    assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
  }
}
