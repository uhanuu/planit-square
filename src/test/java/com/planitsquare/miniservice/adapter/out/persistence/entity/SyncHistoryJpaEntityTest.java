package com.planitsquare.miniservice.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SyncHistoryJpaEntity 테스트")
class SyncHistoryJpaEntityTest {

  @Test
  @DisplayName("성공 이력이 정상적으로 생성된다")
  void 성공_이력이_정상적으로_생성된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    Integer year = 2025;
    SyncExecutionType executionType = SyncExecutionType.SCHEDULED_BATCH;
    Integer syncedCount = 100;
    Long durationNanos = 5_000_000_000L; // 5초
    Integer apiCallCount = 10;

    // When
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, year, executionType, syncedCount, durationNanos, apiCallCount
    );

    // Then
    assertThat(history).isNotNull();
    assertThat(history.getCountry()).isEqualTo(country);
    assertThat(history.getYear()).isEqualTo(2025);
    assertThat(history.getExecutionType()).isEqualTo(SyncExecutionType.SCHEDULED_BATCH);
    assertThat(history.getSyncStatus()).isEqualTo(SyncStatus.SUCCESS);
    assertThat(history.getSyncedCount()).isEqualTo(100);
    assertThat(history.getDurationNanos()).isEqualTo(5_000_000_000L);
    assertThat(history.getApiCallCount()).isEqualTo(10);
    assertThat(history.getSyncedAt()).isNotNull();
  }

  @Test
  @DisplayName("실패 이력이 정상적으로 생성된다")
  void 실패_이력이_정상적으로_생성된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("US", "United States");
    Integer year = 2024;
    SyncExecutionType executionType = SyncExecutionType.MANUAL_EXECUTION;
    String errorMessage = "API 연결 실패";
    Long durationNanos = 1_000_000_000L; // 1초

    // When
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createFailure(
        country, year, executionType, errorMessage, durationNanos
    );

    // Then
    assertThat(history).isNotNull();
    assertThat(history.getSyncStatus()).isEqualTo(SyncStatus.FAILED);
    assertThat(history.getErrorMessage()).isEqualTo("API 연결 실패");
    assertThat(history.getSyncedCount()).isEqualTo(0);
    assertThat(history.isFailed()).isTrue();
  }

  @Test
  @DisplayName("성공 이력 생성 시 필수 필드가 null이면 예외를 던진다")
  void 성공_이력_생성_시_필수_필드가_null이면_예외를_던진다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        null, 2025, SyncExecutionType.API_REFRESH, 100, 5_000_000_000L, 10
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Country must not be null");

    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        country, null, SyncExecutionType.API_REFRESH, 100, 5_000_000_000L, 10
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Year must not be null");

    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        country, 2025, null, 100, 5_000_000_000L, 10
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Execution type must not be null");
  }

  @Test
  @DisplayName("동기화 개수가 음수면 예외를 던진다")
  void 동기화_개수가_음수면_예외를_던진다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.API_REFRESH, -1, 5_000_000_000L, 10
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Synced count must be non-negative");
  }

  @Test
  @DisplayName("실패 이력 생성 시 에러 메시지가 빈 문자열이면 예외를 던진다")
  void 실패_이력_생성_시_에러_메시지가_빈_문자열이면_예외를_던진다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> SyncHistoryJpaEntity.createFailure(
        country, 2025, SyncExecutionType.MANUAL_EXECUTION, "", 1_000_000_000L
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Error message must not be empty");
  }

  @Test
  @DisplayName("비즈니스 메서드가 올바르게 동작한다")
  void 비즈니스_메서드가_올바르게_동작한다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity successHistory = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.API_REFRESH, 100, 5_000_000_000L, 10
    );
    SyncHistoryJpaEntity failedHistory = SyncHistoryJpaEntity.createFailure(
        country, 2025, SyncExecutionType.MANUAL_EXECUTION, "Error", 1_000_000_000L
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
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.SCHEDULED_BATCH, 1000, 5_000_000_000L, 10
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
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.SCHEDULED_BATCH, 100, 0L, 10
    );

    // When
    Double processingRate = history.getProcessingRate();

    // Then
    assertThat(processingRate).isNull();
  }

  @Test
  @DisplayName("평균 API 응답 시간을 올바르게 계산한다_나노초")
  void 평균_API_응답_시간을_올바르게_계산한다_나노초() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.API_REFRESH, 100, 5_000_000_000L, 10
    );

    // When
    Double avgResponseTime = history.getAverageApiResponseTime();

    // Then
    // 5,000,000,000ns / 10 calls = 500,000,000ns/call (500ms)
    assertThat(avgResponseTime).isNotNull();
    assertThat(avgResponseTime).isEqualTo(500_000_000.0);
  }

  @Test
  @DisplayName("평균 API 응답 시간 계산 시 호출 횟수가 0이면 null을 반환한다")
  void 평균_API_응답_시간_계산_시_호출_횟수가_0이면_null을_반환한다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.API_REFRESH, 100, 5_000_000_000L, 0
    );

    // When
    Double avgResponseTime = history.getAverageApiResponseTime();

    // Then
    assertThat(avgResponseTime).isNull();
  }

  @Test
  @DisplayName("Country와 ManyToOne 관계가 유지된다")
  void Country와_ManyToOne_관계가_유지된다() {
    // Given
    CountryJpaEntity country = new CountryJpaEntity("KR", "South Korea");
    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        country, 2025, SyncExecutionType.INITIAL_SYSTEM_LOAD, 100, 5_000_000_000L, 10
    );

    // When & Then
    assertThat(history.getCountry()).isSameAs(country);
    assertThat(history.getCountry().getCode()).isEqualTo("KR");
  }
}
