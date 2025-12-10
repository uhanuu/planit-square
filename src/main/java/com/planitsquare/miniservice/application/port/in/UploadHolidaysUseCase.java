package com.planitsquare.miniservice.application.port.in;

import com.planitsquare.miniservice.application.service.SyncResult;

import java.util.List;

/**
 * 공휴일 업로드 Use Case.
 *
 * <p>외부 API로부터 공휴일 데이터를 조회하여 저장합니다.
 *
 * @since 1.0
 */
public interface UploadHolidaysUseCase {

  /**
   * 공휴일 데이터를 업로드합니다.
   *
   * @param command 업로드 커맨드
   * @return 동기화 결과 리스트
   * @since 1.0
   */
  List<SyncResult> uploadHolidays(UploadHolidayCommand command);
}
