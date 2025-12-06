package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.common.WebAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 휴일 관련 API 컨트롤러.
 *
 * <p>외부 API로부터 휴일 데이터를 가져와 저장하는 기능을 제공합니다.
 *
 * @since 1.0
 */
@Tag(name = "Holiday API", description = "휴일 데이터 관리 API")
@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HolidayController {
  private final UploadHolidaysUseCase uploadHolidaysUseCase;
  private final UploadHolidayRequestMapper requestMapper;

  /**
   * 외부 API로부터 휴일 데이터를 가져와 저장합니다.
   *
   * @param request 휴일 데이터 업로드 요청
   */
  @Operation(
      summary = "휴일 데이터 업로드",
      description = "외부 API로부터 특정 년도의 휴일 데이터를 가져와 데이터베이스에 저장합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "업로드 요청이 성공적으로 접수됨"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 년도 또는 실행 타입)"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PostMapping("/holidays")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void uploadHolidays(@Valid @RequestBody UploadHolidayRequest request) {
    uploadHolidaysUseCase.uploadHolidays(requestMapper.toCommand(request));
  }
}
