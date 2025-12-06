package com.planitsquare.miniservice.adapter.out.persistence.entity;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SyncJobJpaEntity 테스트")
class SyncJobJpaEntityTest {


  @Test
  @DisplayName("executionType이 null이면 예외를 발생시킨다")
  void executionType이_null이면_예외를_발생시킨다() {
    // Given & When & Then
    assertThatThrownBy(() -> SyncJobJpaEntity.start(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Execution type must not be null");
  }

  @Test
  @DisplayName("totalTasks가 음수이면 예외를 발생시킨다")
  void totalTasks가_음수이면_예외를_발생시킨다() {
    // Given & When & Then
    assertThatThrownBy(() -> SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Total tasks must be non-negative");
  }

  @Test
  @DisplayName("다양한 실행 타입으로 Job을 생성할 수 있다")
  void 다양한_실행_타입으로_Job을_생성할_수_있다() {
    // Given & When
    SyncJobJpaEntity scheduledJob = SyncJobJpaEntity.start(SyncExecutionType.SCHEDULED_BATCH);
    SyncJobJpaEntity initialJob = SyncJobJpaEntity.start(SyncExecutionType.INITIAL_SYSTEM_LOAD);
    SyncJobJpaEntity apiRefreshJob = SyncJobJpaEntity.start(SyncExecutionType.API_REFRESH);
    SyncJobJpaEntity manualJob = SyncJobJpaEntity.start(SyncExecutionType.MANUAL_EXECUTION);

    // Then
    assertThat(scheduledJob.getExecutionType()).isEqualTo(SyncExecutionType.SCHEDULED_BATCH);
    assertThat(initialJob.getExecutionType()).isEqualTo(SyncExecutionType.INITIAL_SYSTEM_LOAD);
    assertThat(apiRefreshJob.getExecutionType()).isEqualTo(SyncExecutionType.API_REFRESH);
    assertThat(manualJob.getExecutionType()).isEqualTo(SyncExecutionType.MANUAL_EXECUTION);
  }

}
