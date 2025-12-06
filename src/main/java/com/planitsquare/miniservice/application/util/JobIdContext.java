package com.planitsquare.miniservice.application.util;

/**
 * 현재 스레드의 Job ID를 관리하는 컨텍스트 유틸리티.
 *
 * <p>ThreadLocal을 사용하여 각 스레드마다 독립적인 Job ID를 저장하고 관리합니다.
 * AOP에서 Job을 시작할 때 Job ID를 설정하고, 하위 메서드에서 이를 참조할 수 있습니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * // Job 시작 시 설정
 * JobIdContext.setJobId(jobId);
 *
 * // 하위 메서드에서 조회
 * Long currentJobId = JobIdContext.getJobId();
 *
 * // Job 완료 후 정리
 * JobIdContext.clear();
 * }</pre>
 *
 * @since 1.0
 */
public final class JobIdContext {

  private static final ThreadLocal<Long> jobIdHolder = new ThreadLocal<>();

  /**
   * 유틸리티 클래스이므로 인스턴스화 방지.
   */
  private JobIdContext() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  /**
   * 현재 스레드에 Job ID를 설정합니다.
   *
   * @param jobId 설정할 Job ID
   * @since 1.0
   */
  public static void setJobId(Long jobId) {
    jobIdHolder.set(jobId);
  }

  /**
   * 현재 스레드의 Job ID를 반환합니다.
   *
   * @return Job ID, 설정되지 않았으면 {@code null}
   * @since 1.0
   */
  public static Long getJobId() {
    return jobIdHolder.get();
  }

  /**
   * 현재 스레드의 Job ID를 제거합니다.
   *
   * <p>메모리 누수를 방지하기 위해 Job 완료 후 반드시 호출해야 합니다.
   *
   * @since 1.0
   */
  public static void clear() {
    jobIdHolder.remove();
  }
}
