package com.planitsquare.miniservice.application.port.in;

import com.planitsquare.miniservice.application.service.SyncResult;

import java.util.List;

/**
 * 공휴일 데이터 동기화 Use Case.
 *
 * <p>연간 공휴일 데이터를 자동으로 동기화하는 기능을 제공합니다.
 * 기존 데이터를 삭제하고 외부 API로부터 최신 데이터를 가져와 저장합니다.
 *
 * @since 1.0
 */
public interface SyncHolidayDataUseCase {
  /**
   * 지정된 연도 범위의 모든 국가 공휴일 데이터를 동기화합니다.
   *
   * <p>동기화 프로세스:
   * <ul>
   *   <li>해당 연도의 기존 공휴일 데이터를 삭제합니다</li>
   *   <li>모든 국가의 최신 공휴일 데이터를 외부 API로부터 조회합니다</li>
   *   <li>조회된 데이터를 데이터베이스에 저장합니다</li>
   * </ul>
   *
   * @param command 동기화 커맨드 (연도 및 실행 타입 포함)
   * @return 국가별·연도별 동기화 결과 목록
   * @since 1.0
   */
  List<SyncResult> syncAnnualHolidays(UploadHolidayCommand command);
}
