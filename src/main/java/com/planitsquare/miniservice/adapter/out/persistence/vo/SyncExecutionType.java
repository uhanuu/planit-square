package com.planitsquare.miniservice.adapter.out.persistence.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 동기화 작업의 실행 타입을 나타내는 Enum.
 *
 * <p>동기화 작업이 어떤 방식으로, 무엇을 목적으로 실행되었는지를 통합하여 표현합니다.
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SyncExecutionType {
  /**
   * 시스템 최초 시작 시 전체 데이터 자동 적재.
   */
  INITIAL_SYSTEM_LOAD(
      "최초 시스템 적재",
      "애플리케이션 시작 시 시스템에 의해 전체 데이터 자동 적재",
      true
  ),

  /**
   * 스케줄러에 의한 배치 자동 동기화.
   */
  SCHEDULED_BATCH(
      "스케줄 배치",
      "스케줄러에 의해 주기적으로 자동 실행되는 배치 동기화",
      true
  ),

  /**
   * 외부 API 호출에 의한 데이터 갱신.
   */
  API_REFRESH(
      "API 갱신",
      "외부 API 호출 요청에 의한 데이터 갱신",
      false
  ),

  /**
   * 관리자가 수동으로 실행한 동기화.
   */
  MANUAL_EXECUTION(
      "수동 실행",
      "관리자가 수동으로 실행한 동기화 작업",
      false
  ),

  /**
   * 특정 이벤트 발생에 의한 동기화.
   */
  EVENT_TRIGGERED(
      "이벤트 트리거",
      "특정 시스템 이벤트 발생에 의해 자동 실행된 동기화",
      true
  );

  private final String displayName;
  private final String description;
  private final boolean automatic;

  /**
   * 자동 실행 타입인지 확인합니다.
   *
   * @return 자동 실행이면 true
   */
  public boolean isAutomatic() {
    return this.automatic;
  }

  /**
   * 수동 실행 타입인지 확인합니다.
   *
   * @return 수동 실행이면 true
   */
  public boolean isManual() {
    return !this.automatic;
  }
}
