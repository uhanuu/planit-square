package com.planitsquare.miniservice.adapter.out.persistence.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SyncExecutionType 테스트.
 *
 * @since 1.0
 */
@DisplayName("SyncExecutionType 테스트")
class SyncExecutionTypeTest {

  @Test
  @DisplayName("자동_실행_타입을_올바르게_판단한다")
  void 자동_실행_타입을_올바르게_판단한다() {
    // Given & When & Then
    assertThat(SyncExecutionType.INITIAL_SYSTEM_LOAD.isAutomatic()).isTrue();
    assertThat(SyncExecutionType.SCHEDULED_BATCH.isAutomatic()).isTrue();
    assertThat(SyncExecutionType.EVENT_TRIGGERED.isAutomatic()).isTrue();
    assertThat(SyncExecutionType.API_REFRESH.isAutomatic()).isFalse();
    assertThat(SyncExecutionType.MANUAL_EXECUTION.isAutomatic()).isFalse();
  }

  @Test
  @DisplayName("수동_실행_타입을_올바르게_판단한다")
  void 수동_실행_타입을_올바르게_판단한다() {
    // Given & When & Then
    assertThat(SyncExecutionType.API_REFRESH.isManual()).isTrue();
    assertThat(SyncExecutionType.MANUAL_EXECUTION.isManual()).isTrue();
    assertThat(SyncExecutionType.INITIAL_SYSTEM_LOAD.isManual()).isFalse();
    assertThat(SyncExecutionType.SCHEDULED_BATCH.isManual()).isFalse();
    assertThat(SyncExecutionType.EVENT_TRIGGERED.isManual()).isFalse();
  }

  @Test
  @DisplayName("모든_타입이_displayName을_가진다")
  void 모든_타입이_displayName을_가진다() {
    // Given & When & Then
    for (SyncExecutionType type : SyncExecutionType.values()) {
      assertThat(type.getDisplayName()).isNotBlank();
      assertThat(type.getDescription()).isNotBlank();
    }
  }

  @Test
  @DisplayName("모든_타입이_5개이다")
  void 모든_타입이_5개이다() {
    // Given & When & Then
    assertThat(SyncExecutionType.values()).hasSize(5);
  }
}
