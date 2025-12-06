package com.planitsquare.miniservice.adapter.in.web;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import org.springframework.stereotype.Component;

/**
 * UploadHolidayRequest를 UploadHolidayCommand로 변환하는 Mapper.
 *
 * <p>Web 계층의 Request DTO를 Application 계층의 Command로 변환합니다.
 *
 * @since 1.0
 */
@Component
public class UploadHolidayRequestMapper {

  /**
   * UploadHolidayRequest를 UploadHolidayCommand로 변환합니다.
   *
   * @param request 휴일 업로드 요청 DTO
   * @return UploadHolidayCommand
   * @throws IllegalArgumentException executionType이 유효하지 않은 경우
   */
  public UploadHolidayCommand toCommand(UploadHolidayRequest request) {
    return new UploadHolidayCommand(
        request.year(),
        SyncExecutionType.findByName(request.executionType())
    );
  }
}
