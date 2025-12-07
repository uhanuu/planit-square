package com.planitsquare.miniservice.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@DisplayName("SyncHistoryJpaEntity 테스트")
class SyncHistoryJpaEntityTest {

  @Test
  @DisplayName("성공 이력이 정상적으로 생성된다")
  void 성공_이력이_정상적으로_생성된다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    Integer year = 2025;
    Integer syncedCount = 100;
    Long durationMillis = 5000L;
    LocalDateTime syncedAt = LocalDateTime.now();

    // When
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, year, syncedCount, durationMillis,syncedAt
    );

    // Then
    assertThat(history).isNotNull();
    assertThat(history.getSyncJob()).isEqualTo(job);
    assertThat(history.getCountry()).isEqualTo(country);
    assertThat(history.getYear()).isEqualTo(2025);
    assertThat(history.getSyncStatus()).isEqualTo(SyncStatus.SUCCESS);
    assertThat(history.getSyncedCount()).isEqualTo(100);
    assertThat(history.getDurationMillis()).isEqualTo(5000L);
    assertThat(history.getSyncedAt()).isEqualTo(syncedAt);
  }

  @Test
  @DisplayName("실패 이력이 정상적으로 생성된다")
  void 실패_이력이_정상적으로_생성된다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.MANUAL_EXECUTION);
    CountryJpaEntity country = new CountryJpaEntity("US", "United States");
    Integer year = 2024;
    String errorMessage = "API 연결 실패";
    Long durationMillis = 1000L; // 1초
    LocalDateTime syncedAt = LocalDateTime.now();

    // When
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createFailure(
        job, country, year, errorMessage, durationMillis, syncedAt
    );

    // Then
    assertThat(history).isNotNull();
    assertThat(history.getSyncJob()).isEqualTo(job);
    assertThat(history.getSyncStatus()).isEqualTo(SyncStatus.FAILED);
    assertThat(history.getErrorMessage()).isEqualTo("API 연결 실패");
    assertThat(history.getSyncedCount()).isEqualTo(0);
    assertThat(history.isFailed()).isTrue();
    assertThat(history.getSyncedAt()).isEqualTo(syncedAt);
  }

  @Test
  @DisplayName("성공 이력 생성 시 필수 필드가 null이면 예외를 던진다")
  void 성공_이력_생성_시_필수_필드가_null이면_예외를_던진다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.API_REFRESH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        null, country, 2025, 100, 5000L, LocalDateTime.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Job must not be null");

    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        job, null, 2025, 100, 5000L, LocalDateTime.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Country must not be null");

    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        job, country, null, 100, 5000L, LocalDateTime.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Year must not be null");
  }

  @Test
  @DisplayName("동기화 개수가 음수면 예외를 던진다")
  void 동기화_개수가_음수면_예외를_던진다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.API_REFRESH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, -1, 5000L, LocalDateTime.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Synced count must be non-negative");
  }

  @Test
  @DisplayName("실패 이력 생성 시 에러 메시지가 빈 문자열이면 예외를 던진다")
  void 실패_이력_생성_시_에러_메시지가_빈_문자열이면_예외를_던진다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.MANUAL_EXECUTION);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createFailure(
        job, country, 2025, "", 1000L, LocalDateTime.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Error message must not be empty");
  }

  @Test
  @DisplayName("비즈니스 메서드가 올바르게 동작한다")
  void 비즈니스_메서드가_올바르게_동작한다() {
    // Given
    SyncJobJpaEntity job1 = SyncJobJpaEntity.start(SyncExecutionType.API_REFRESH);
    SyncJobJpaEntity job2 = SyncJobJpaEntity.start(SyncExecutionType.MANUAL_EXECUTION);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity successHistory = SyncHistoryJpaEntity.createSuccess(
        job1, country, 2025, 100, 5000L, LocalDateTime.now()
    );
    SyncHistoryJpaEntity failedHistory = SyncHistoryJpaEntity.createFailure(
        job2, country, 2025, "Error", 1000L, LocalDateTime.now()
    );

    // When & Then
    assertThat(successHistory.isSuccess()).isTrue();
    assertThat(successHistory.isFailed()).isFalse();

    assertThat(failedHistory.isSuccess()).isFalse();
    assertThat(failedHistory.isFailed()).isTrue();
  }

  @Test
  @DisplayName("처리 속도를 올바르게 계산한다")
  void 처리_속도를_올바르게_계산한다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, 1000, 5000L, LocalDateTime.now()
    );

    // When
    Double processingRate = history.getProcessingRate();

    // Then
    // 1000 records / 5 seconds = 200 records/sec
    assertThat(processingRate).isNotNull();
    assertThat(processingRate).isEqualTo(200.0);
  }

  @Test
  @DisplayName("처리 속도 계산 시 소요 시간이 0이면 null을 반환한다")
  void 처리_속도_계산_시_소요_시간이_0이면_null을_반환한다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, 100, 0L, LocalDateTime.now()
    );

    // When
    Double processingRate = history.getProcessingRate();

    // Then
    assertThat(processingRate).isNull();
  }

  @Test
  @DisplayName("밀리초 단위로 시간을 저장한다")
  void 밀리초_단위로_시간을_저장한다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.API_REFRESH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    Long durationMillis = 5000L; // 5초

    // When
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, 100, durationMillis, LocalDateTime.now()
    );

    // Then
    assertThat(history.getDurationMillis()).isEqualTo(5000L);
  }

  @Test
  @DisplayName("Country와 ManyToOne 관계가 유지된다")
  void Country와_ManyToOne_관계가_유지된다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.INITIAL_SYSTEM_LOAD);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, 100, 5000L, LocalDateTime.now()
    );

    // When & Then
    assertThat(history.getCountry()).isSameAs(country);
    assertThat(history.getCountry().getCode()).isEqualTo("KR");
  }

  @Test
  @DisplayName("Job과 ManyToOne 관계가 유지된다")
  void Job과_ManyToOne_관계가_유지된다() {
    // Given
    SyncJobJpaEntity job = SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH);
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job, country, 2025, 100, 5000L, LocalDateTime.now()
    );

    // When & Then
    assertThat(history.getSyncJob()).isSameAs(job);
    assertThat(history.getSyncJob().getExecutionType()).isEqualTo(SyncExecutionType.SCHEDULED_BATCH);
  }
}
