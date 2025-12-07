package com.planitsquare.miniservice.adapter.out.persistence.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JobStatus 테스트")
class JobStatusTest {

  @Test
  @DisplayName("RUNNING 상태는 실행 중 상태이다")
  void RUNNING_상태는_실행_중_상태이다() {
    // Given & When
    JobStatus status = JobStatus.RUNNING;

    // Then
    assertThat(status.isRunning()).isTrue();
    assertThat(status.isCompleted()).isFalse();
    assertThat(status.isFailed()).isFalse();
  }

  @Test
  @DisplayName("COMPLETED 상태는 완료 상태이다")
  void COMPLETED_상태는_완료_상태이다() {
    // Given & When
    JobStatus status = JobStatus.COMPLETED;

    // Then
    assertThat(status.isRunning()).isFalse();
    assertThat(status.isCompleted()).isTrue();
    assertThat(status.isFailed()).isFalse();
  }

  @Test
  @DisplayName("FAILED 상태는 실패 상태이다")
  void FAILED_상태는_실패_상태이다() {
    // Given & When
    JobStatus status = JobStatus.FAILED;

    // Then
    assertThat(status.isRunning()).isFalse();
    assertThat(status.isCompleted()).isFalse();
    assertThat(status.isFailed()).isTrue();
  }

  @Test
  @DisplayName("모든 상태는 displayName을 가진다")
  void 모든_상태는_displayName을_가진다() {
    // Given & When & Then
    assertThat(JobStatus.RUNNING.getDisplayName()).isEqualTo("실행 중");
    assertThat(JobStatus.COMPLETED.getDisplayName()).isEqualTo("완료");
    assertThat(JobStatus.FAILED.getDisplayName()).isEqualTo("실패");
  }

}
