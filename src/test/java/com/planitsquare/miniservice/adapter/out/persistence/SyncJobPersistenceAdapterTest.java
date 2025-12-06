package com.planitsquare.miniservice.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncJobJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncHistoryJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncJobJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.vo.JobStatus;
import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.exception.JobAlreadyRunningException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SyncJobPersistenceAdapter 테스트")
class SyncJobPersistenceAdapterTest {

  @Autowired
  private SyncJobPersistenceAdapter syncJobPersistenceAdapter;

  @Autowired
  private SyncJobJpaRepository syncJobJpaRepository;

  @Autowired
  private SyncHistoryJpaRepository syncHistoryJpaRepository;

  @AfterEach
  void tearDown() {
    // 각 테스트 후 모든 데이터 삭제 (외래 키 제약 조건 고려)
    syncHistoryJpaRepository.deleteAllInBatch();
    syncJobJpaRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("RUNNING 상태의 Job이 이미 존재하면 예외를 발생시킨다")
  void RUNNING_상태의_Job이_이미_존재하면_예외를_발생시킨다() {
    // Given
    syncJobPersistenceAdapter.startJob(SyncExecutionType.SCHEDULED_BATCH);

    // When & Then
    assertThatThrownBy(() ->
        syncJobPersistenceAdapter.startJob(SyncExecutionType.API_REFRESH)
    )
        .isInstanceOf(JobAlreadyRunningException.class)
        .hasMessageContaining("이미 실행 중인 Job이 존재합니다");
  }

}
