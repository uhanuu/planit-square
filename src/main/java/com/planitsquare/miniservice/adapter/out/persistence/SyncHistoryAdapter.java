package com.planitsquare.miniservice.adapter.out.persistence;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncHistoryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.SyncJobJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.mapper.CountryMapper;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncHistoryJpaRepository;
import com.planitsquare.miniservice.adapter.out.persistence.repository.SyncJobJpaRepository;
import com.planitsquare.miniservice.application.port.out.RecordSyncHistoryPort;
import com.planitsquare.miniservice.common.PersistenceAdapter;
import com.planitsquare.miniservice.domain.vo.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 동기화 이력 기록을 위한 Persistence Adapter.
 *
 * <p>RecordSyncHistoryPort 구현체로, 동기화 작업의 성공/실패 이력을 데이터베이스에 저장합니다.
 *
 * @since 1.0
 */
@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class SyncHistoryAdapter implements RecordSyncHistoryPort {

  private final SyncHistoryJpaRepository syncHistoryJpaRepository;
  private final SyncJobJpaRepository syncJobJpaRepository;
  private final CountryMapper countryMapper;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recordSuccess(
      Long jobId,
      Country country,
      Integer year,
      Integer syncedCount,
      Long durationMillis,
      LocalDateTime syncedAt
  ) {
    SyncJobJpaEntity job = syncJobJpaRepository.findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    CountryJpaEntity countryEntity = countryMapper.toEntity(country);

    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createSuccess(
        job,
        countryEntity,
        year,
        syncedCount,
        durationMillis,
        syncedAt
    );

    syncHistoryJpaRepository.save(history);

    log.debug("동기화 성공 이력 기록 완료 - Job ID: {}, 국가: {}, 연도: {}, 건수: {}",
        jobId, country.getCode(), year, syncedCount);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recordFailure(
      Long jobId,
      Country country,
      Integer year,
      String errorMessage,
      Long durationMillis,
      LocalDateTime syncedAt
  ) {
    SyncJobJpaEntity job = syncJobJpaRepository.findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    CountryJpaEntity countryEntity = countryMapper.toEntity(country);

    SyncHistoryJpaEntity history = SyncHistoryJpaEntity.createFailure(
        job,
        countryEntity,
        year,
        errorMessage,
        durationMillis,
        syncedAt
    );

    syncHistoryJpaRepository.save(history);

    log.warn("동기화 실패 이력 기록 완료 - Job ID: {}, 국가: {}, 연도: {}, 에러: {}",
        jobId, country.getCode(), year, errorMessage);
  }
}
