package com.planitsquare.miniservice.adapter.out.persistence.entity;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    @Index(name = "idx_sync_job", columnList = "job_id"),
    @Index(name = "idx_sync_country", columnList = "country_code"),
    @Index(name = "idx_sync_year", columnList = "`year`"),
    @Index(name = "idx_sync_status", columnList = "sync_status"),
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
   * 이 History가 속한 동기화 Job.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private SyncJobJpaEntity syncJob;

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
   * 동기화 소요 시간 (밀리초).
   */
  @Column(name = "duration_millis")
  private Long durationMillis;

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

  @Builder
  private SyncHistoryJpaEntity(
      Long id,
      SyncJobJpaEntity syncJob,
      CountryJpaEntity country,
      Integer year,
      SyncStatus syncStatus,
      Integer syncedCount,
      Long durationMillis,
      String errorMessage,
      LocalDateTime syncedAt
  ) {
    this.id = id;
    this.syncJob = syncJob;
    this.country = country;
    this.year = year;
    this.syncStatus = syncStatus;
    this.syncedCount = syncedCount;
    this.durationMillis = durationMillis;
    this.errorMessage = errorMessage;
    this.syncedAt = syncedAt;
  }

  /**
   * 동기화 성공 이력을 생성합니다.
   *
   * @param job            동기화 Job
   * @param country        국가
   * @param year           년도
   * @param syncedCount    동기화된 개수
   * @param durationMillis 소요 시간 (밀리초)
   * @param syncedAt       동기화 완료 시간
   * @return SyncHistoryJpaEntity
   */
  public static SyncHistoryJpaEntity createSuccess(
      SyncJobJpaEntity job,
      CountryJpaEntity country,
      Integer year,
      Integer syncedCount,
      Long durationMillis,
      LocalDateTime syncedAt
  ) {
    Assert.notNull(job, "Job must not be null");
    Assert.notNull(country, "Country must not be null");
    Assert.notNull(year, "Year must not be null");
    Assert.notNull(durationMillis, "Duration millis must not be null");
    Assert.isTrue(syncedCount >= 0, "Synced count must be non-negative");
    Assert.notNull(syncedAt, "Synced at must not be null");

    return SyncHistoryJpaEntity.builder()
        .syncJob(job)
        .country(country)
        .year(year)
        .syncStatus(SyncStatus.SUCCESS)
        .syncedCount(syncedCount)
        .durationMillis(durationMillis)
        .syncedAt(syncedAt)
        .build();
  }

  /**
   * 동기화 실패 이력을 생성합니다.
   *
   * @param job            동기화 Job
   * @param country        국가
   * @param year           년도
   * @param errorMessage   에러 메시지
   * @param durationMillis 소요 시간 (밀리초)
   * @param syncedAt       동기화 완료 시간
   * @return SyncHistoryJpaEntity
   */
  public static SyncHistoryJpaEntity createFailure(
      SyncJobJpaEntity job,
      CountryJpaEntity country,
      Integer year,
      String errorMessage,
      Long durationMillis,
      LocalDateTime syncedAt
  ) {
    Assert.notNull(job, "Job must not be null");
    Assert.notNull(country, "Country must not be null");
    Assert.notNull(year, "Year must not be null");
    Assert.notNull(durationMillis, "Duration millis must not be null");
    Assert.hasText(errorMessage, "Error message must not be empty");
    Assert.notNull(syncedAt, "Synced at must not be null");

    return SyncHistoryJpaEntity.builder()
        .syncJob(job)
        .country(country)
        .year(year)
        .syncStatus(SyncStatus.FAILED)
        .durationMillis(durationMillis)
        .errorMessage(errorMessage)
        .syncedAt(syncedAt)
        .build();
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
    if (durationMillis == null || durationMillis == 0 || syncedCount == 0) {
      return null;
    }
    double durationSeconds = durationMillis / 1000.0;
    return syncedCount / durationSeconds;
  }

}
