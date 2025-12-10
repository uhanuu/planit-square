package com.planitsquare.miniservice.application.service;

import java.util.List;

/**
 * 비동기 동기화 작업의 통계 정보를 나타내는 Value Object.
 *
 * <p>전체 작업 수, 성공/실패 건수를 집계하여 Job 완료 시 DB에 기록합니다.
 *
 * @param totalTasks 전체 작업 수
 * @param successCount 성공한 작업 수
 * @param failureCount 실패한 작업 수
 * @since 1.0
 */
public record SyncStats(
    int totalTasks,
    int successCount,
    int failureCount
) {

  /**
   * SyncResult 리스트로부터 통계를 생성합니다.
   *
   * @param results 동기화 결과 리스트
   * @return 집계된 통계
   * @since 1.0
   */
  public static SyncStats from(List<SyncResult> results) {
    int total = results.size();
    long successCount = results.stream()
        .filter(SyncResult::isSuccess)
        .count();
    int failureCount = total - (int) successCount;

    return new SyncStats(total, (int) successCount, failureCount);
  }

  /**
   * 모든 작업이 성공했는지 확인합니다.
   *
   * @return 모든 작업이 성공하면 {@code true}
   * @since 1.0
   */
  public boolean isAllSuccess() {
    return failureCount == 0;
  }

  /**
   * 모든 작업이 실패했는지 확인합니다.
   *
   * @return 모든 작업이 실패하면 {@code true}
   * @since 1.0
   */
  public boolean isAllFailure() {
    return successCount == 0;
  }

  /**
   * 일부 성공, 일부 실패했는지 확인합니다.
   *
   * @return 부분 성공이면 {@code true}
   * @since 1.0
   */
  public boolean isPartialSuccess() {
    return successCount > 0 && failureCount > 0;
  }

  /**
   * 통계 정보를 로그 형식의 문자열로 반환합니다.
   *
   * @return 통계 문자열
   * @since 1.0
   */
  public String toLogString() {
    return String.format("성공: %d, 실패: %d, 전체: %d",
        successCount, failureCount, totalTasks);
  }
}
