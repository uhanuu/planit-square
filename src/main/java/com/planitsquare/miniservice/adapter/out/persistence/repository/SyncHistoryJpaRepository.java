package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 동기화 이력 JPA Repository.
 *
 * @since 1.0
 */
public interface SyncHistoryJpaRepository extends JpaRepository<SyncHistoryJpaEntity, Long> {
}
