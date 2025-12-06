package com.planitsquare.miniservice.adapter.out.persistence.entity;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 데이터 동기화 이력을 저장하는 JPA Entity.
 *
 * <p>외부 API로부터 특정 국가의 특정 년도 휴일 데이터를 동기화한 이력을 추적합니다.
 * 대용량 데이터(수천만 건) 처리를 고려하여 설계되었습니다.
 *
 * @since 1.0
 */
@Entity
@Table(name = "sync_history", indexes = {
    @Index(name = "idx_sync_country", columnList = "country_code"),
    @Index(name = "idx_sync_year", columnList = "`year`"),
    @Index(name = "idx_sync_status", columnList = "sync_status"),
    @Index(name = "idx_execution_type", columnList = "execution_type"),
    @Index(name = "idx_synced_at", columnList = "synced_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SyncHistoryJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sync_history_id")
  private Long id;

  /**
   * 동기화 대상 국가.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_code", referencedColumnName = "code", nullable = false)
  private CountryJpaEntity country;

  /**
   * 동기화 대상 년도.
   */
  @Column(name = "`year`", nullable = false)
  private Integer year;

  /**
   * 동기화 실행 타입 (작업 종류 + 트리거 방식 통합).
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "execution_type", nullable = false, length = 30)
  private SyncExecutionType executionType;

  /**
   * 동기화 상태.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "sync_status", nullable = false, length = 20)
  private SyncStatus syncStatus;

  /**
   * 동기화된 레코드 수.
   */
  @Column(name = "synced_count", nullable = false)
  private Integer syncedCount = 0;

  /**
   * 외부 API 호출 횟수.
   */
  @Column(name = "api_call_count")
  private Integer apiCallCount = 0;

  /**
   * 동기화 소요 시간 (나노초).
   */
  @Column(name = "duration_nanos")
  private Long durationNanos;

  /**
   * 에러 메시지.
   */
  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  /**
   * 동기화 완료 시간.
   */
  @Column(name = "synced_at")
  private LocalDateTime syncedAt;

  /**
   * 동기화 성공 이력을 생성합니다.
   *
   * @param country       국가
   * @param year          년도
   * @param executionType 실행 타입
   * @param syncedCount   동기화된 개수
   * @param durationNanos 소요 시간 (나노초)
   * @param apiCallCount  외부 API 호출 횟수
   * @return SyncHistoryJpaEntity
   */
  public static SyncHistoryJpaEntity createSuccess(
      CountryJpaEntity country,
      Integer year,
      SyncExecutionType executionType,
      Integer syncedCount,
      Long durationNanos,
      Integer apiCallCount
  ) {
    Assert.notNull(country, "Country must not be null");
    Assert.notNull(year, "Year must not be null");
    Assert.notNull(executionType, "Execution type must not be null");
    Assert.isTrue(syncedCount >= 0, "Synced count must be non-negative");

    SyncHistoryJpaEntity history = new SyncHistoryJpaEntity();
    history.country = country;
    history.year = year;
    history.executionType = executionType;
    history.syncStatus = SyncStatus.SUCCESS;
    history.syncedCount = syncedCount;
    history.durationNanos = durationNanos;
    history.apiCallCount = apiCallCount;
    history.syncedAt = LocalDateTime.now();
    return history;
  }

  /**
   * 동기화 실패 이력을 생성합니다.
   *
   * @param country       국가
   * @param year          년도
   * @param executionType 실행 타입
   * @param errorMessage  에러 메시지
   * @param durationNanos 소요 시간 (나노초)
   * @return SyncHistoryJpaEntity
   */
  public static SyncHistoryJpaEntity createFailure(
      CountryJpaEntity country,
      Integer year,
      SyncExecutionType executionType,
      String errorMessage,
      Long durationNanos
  ) {
    Assert.notNull(country, "Country must not be null");
    Assert.notNull(year, "Year must not be null");
    Assert.notNull(executionType, "Execution type must not be null");
    Assert.hasText(errorMessage, "Error message must not be empty");

    SyncHistoryJpaEntity history = new SyncHistoryJpaEntity();
    history.country = country;
    history.year = year;
    history.executionType = executionType;
    history.syncStatus = SyncStatus.FAILED;
    history.syncedCount = 0;
    history.errorMessage = errorMessage;
    history.durationNanos = durationNanos;
    history.syncedAt = LocalDateTime.now();
    return history;
  }

  /**
   * 성공 상태인지 확인합니다.
   *
   * @return 성공이면 true
   */
  public boolean isSuccess() {
    return this.syncStatus.isSuccess();
  }

  /**
   * 실패 상태인지 확인합니다.
   *
   * @return 실패면 true
   */
  public boolean isFailed() {
    return this.syncStatus.isFailed();
  }

  /**
   * 처리 속도를 계산합니다 (레코드/초).
   *
   * @return 처리 속도, 계산할 수 없으면 null
   */
  public Double getProcessingRate() {
    if (durationNanos == null || durationNanos == 0 || syncedCount == 0) {
      return null;
    }
    double durationSeconds = durationNanos / 1_000_000_000.0;
    return syncedCount / durationSeconds;
  }

  /**
   * 평균 외부 API 응답 시간을 계산합니다 (나노초/호출).
   *
   * @return 평균 응답 시간 (나노초), 계산할 수 없으면 null
   */
  public Double getAverageApiResponseTime() {
    if (durationNanos == null || apiCallCount == null || apiCallCount == 0) {
      return null;
    }
    return (double) durationNanos / apiCallCount;
  }
}
