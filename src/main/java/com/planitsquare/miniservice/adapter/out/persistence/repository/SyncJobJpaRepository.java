package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncJobJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.vo.JobStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 동기화 Job JPA Repository.
 *
 * @since 1.0
 */
public interface SyncJobJpaRepository extends JpaRepository<SyncJobJpaEntity, Long> {

  /**
   * 특정 상태의 Job이 존재하는지 확인합니다.
   *
   * @param status Job 상태
   * @return 존재하면 {@code true}
   * @since 1.0
   */
  boolean existsByStatus(JobStatus status);

  /**
   * 특정 상태의 가장 최근 Job을 조회합니다.
   *
   * @param status Job 상태
   * @return 가장 최근 Job, 없으면 {@link Optional#empty()}
   * @since 1.0
   */
  Optional<SyncJobJpaEntity> findFirstByStatusOrderByStartTimeDesc(JobStatus status);
}
