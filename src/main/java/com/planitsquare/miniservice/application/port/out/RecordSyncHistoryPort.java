package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.vo.Country;

import java.time.LocalDateTime;

/**
 * 동기화 이력을 기록하기 위한 Port 인터페이스.
 *
 * <p>외부 API를 통한 공휴일 데이터 동기화 작업의 성공/실패 이력을 저장합니다.
 *
 * @since 1.0
 */
public interface RecordSyncHistoryPort {

  /**
   * 동기화 성공 이력을 기록합니다.
   *
   * @param jobId 동기화 Job ID
   * @param country 동기화 대상 국가
   * @param year 동기화 대상 연도
   * @param syncedCount 동기화된 레코드 수
   * @param durationMillis 소요 시간 (밀리초)
   * @param syncedAt       동기화 완료 시간
   * @since 1.0
   */
  void recordSuccess(
      Long jobId,
      Country country,
      Integer year,
      Integer syncedCount,
      Long durationMillis,
      LocalDateTime syncedAt
  );

  /**
   * 동기화 실패 이력을 기록합니다.
   *
   * @param jobId 동기화 Job ID
   * @param country 동기화 대상 국가
   * @param year 동기화 대상 연도
   * @param errorMessage 에러 메시지
   * @param durationMillis 소요 시간 (밀리초)
   * @param syncedAt       동기화 완료 시간
   * @since 1.0
   */
  void recordFailure(
      Long jobId,
      Country country,
      Integer year,
      String errorMessage,
      Long durationMillis,
      LocalDateTime syncedAt
  );
}
