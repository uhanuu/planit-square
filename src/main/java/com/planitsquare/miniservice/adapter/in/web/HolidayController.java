package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.in.web.dto.request.UploadHolidayRequest;
import com.planitsquare.miniservice.adapter.in.web.dto.response.DeleteHolidayResponse;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysCommand;
import com.planitsquare.miniservice.application.port.in.DeleteHolidaysUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.common.WebAdapter;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  private final DeleteHolidaysUseCase deleteHolidaysUseCase;

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
  public ResponseEntity<Void> uploadHolidays(@Valid @RequestBody UploadHolidayRequest request) {
    uploadHolidaysUseCase.uploadHolidays(
        new UploadHolidayCommand(request.year(), SyncExecutionType.MANUAL_EXECUTION)
    );

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  /**
   * 특정 연도와 국가의 공휴일 데이터를 삭제합니다.
   *
   * @param year 삭제할 연도
   * @param countryCode 삭제할 국가 코드
   * @return 삭제된 공휴일 건수를 포함한 응답
   */
  @Operation(
      summary = "공휴일 데이터 삭제",
      description = "특정 연도와 국가의 공휴일 데이터를 데이터베이스에서 삭제합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 년도)"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 국가 코드"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @DeleteMapping("/holidays/{year}/{countryCode}")
  public ResponseEntity<DeleteHolidayResponse> deleteHolidays(
      @Parameter(description = "삭제할 연도", example = "2024", required = true)
      @PathVariable Integer year,
      @Parameter(description = "삭제할 국가 코드", example = "KR", required = true)
      @PathVariable String countryCode
  ) {
    DeleteHolidaysCommand command = new DeleteHolidaysCommand(year, new CountryCode(countryCode));
    int deletedCount = deleteHolidaysUseCase.deleteHolidays(command);

    return ResponseEntity.ok(new DeleteHolidayResponse(deletedCount));
  }
}
