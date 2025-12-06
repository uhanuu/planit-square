package com.planitsquare.miniservice.adapter.out.persistence.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 동기화 작업의 상태를 나타내는 Enum.
 *
 * <p>데이터 동기화 작업의 진행 상태를 추적하기 위한 열거형입니다.
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SyncStatus {
  /**
   * 동기화 작업이 성공적으로 완료되었습니다.
   */
  SUCCESS("성공", "모든 데이터가 정상적으로 동기화됨"),

  /**
   * 동기화 작업이 실패했습니다.
   */
  FAILED("실패", "동기화 중 오류 발생");

  private final String displayName;
  private final String description;

  /**
   * 성공 상태인지 확인합니다.
   *
   * @return 성공이면 true
   */
  public boolean isSuccess() {
    return this == SyncStatus.SUCCESS;
  }

  /**
   * 실패 상태인지 확인합니다.
   *
   * @return 실패면 true
   */
  public boolean isFailed() {
    return this == SyncStatus.FAILED;
  }
}
