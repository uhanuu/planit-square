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
   * 테이블에 하나 이상의 Job 데이터가 존재하는지 확인합니다.
   * <p>
   * 이 메서드는 기본 키(id)가 null이 아닌 레코드가 있는지를 조회하여,
   * 테이블에 데이터가 하나라도 존재하면 {@code true} 를 반환합니다.
   * 전체 레코드를 대상으로 카운트를 수행하는 {@link JpaRepository#count()} 대비
   * 더 빠르게 존재 여부를 판단할 수 있습니다.
   *
   * @return Job 데이터가 하나 이상 존재하면 {@code true}, 그렇지 않으면 {@code false}
   * @since 1.0
   */
  boolean existsByIdIsNotNull();
}
