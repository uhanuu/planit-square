-- ============================================
-- SyncHistoryJpaEntity 리팩터링 마이그레이션
-- ============================================
-- 작업: SyncType + TriggerType → SyncExecutionType 통합
--       durationMillis → durationNanos 변경
-- 작성일: 2025-12-06
-- ============================================

-- 1. 새 컬럼 추가
ALTER TABLE sync_history ADD COLUMN execution_type VARCHAR(30);
ALTER TABLE sync_history ADD COLUMN duration_nanos BIGINT;

-- 2. 기존 데이터 마이그레이션
UPDATE sync_history
SET execution_type = CASE
  WHEN sync_type = 'INITIAL_LOAD' AND trigger_type = 'SYSTEM' THEN 'INITIAL_SYSTEM_LOAD'
  WHEN sync_type = 'BATCH' AND trigger_type = 'SCHEDULED_BATCH' THEN 'SCHEDULED_BATCH'
  WHEN sync_type = 'REFRESH' AND trigger_type = 'API' THEN 'API_REFRESH'
  WHEN sync_type = 'MANUAL' AND trigger_type = 'MANUAL' THEN 'MANUAL_EXECUTION'
  WHEN trigger_type = 'EVENT' THEN 'EVENT_TRIGGERED'
  ELSE 'MANUAL_EXECUTION'
END
WHERE execution_type IS NULL;

-- 3. duration_millis를 duration_nanos로 변환 (밀리초 → 나노초)
UPDATE sync_history
SET duration_nanos = duration_millis * 1000000
WHERE duration_millis IS NOT NULL AND duration_nanos IS NULL;

-- 4. NOT NULL 제약조건 추가
ALTER TABLE sync_history ALTER COLUMN execution_type SET NOT NULL;

-- 5. 새 인덱스 추가
CREATE INDEX idx_execution_type ON sync_history(execution_type);

-- 6. 기존 인덱스 삭제
DROP INDEX IF EXISTS idx_sync_type;
DROP INDEX IF EXISTS idx_trigger_type;

-- 7. 기존 컬럼 삭제
ALTER TABLE sync_history DROP COLUMN IF EXISTS sync_type;
ALTER TABLE sync_history DROP COLUMN IF EXISTS trigger_type;
ALTER TABLE sync_history DROP COLUMN IF EXISTS duration_millis;

-- 8. 컬럼 주석 추가 (H2는 COMMENT 지원 안 함, PostgreSQL/MySQL용)
-- COMMENT ON COLUMN sync_history.execution_type IS '동기화 실행 타입 (작업 종류 + 트리거 방식 통합)';
-- COMMENT ON COLUMN sync_history.duration_nanos IS '동기화 소요 시간 (나노초)';
-- COMMENT ON COLUMN sync_history.api_call_count IS '외부 API 호출 횟수';

-- ============================================
-- 롤백 스크립트 (필요시 사용)
-- ============================================
-- ALTER TABLE sync_history ADD COLUMN sync_type VARCHAR(20);
-- ALTER TABLE sync_history ADD COLUMN trigger_type VARCHAR(20);
-- ALTER TABLE sync_history ADD COLUMN duration_millis BIGINT;
--
-- UPDATE sync_history
-- SET sync_type = 'MANUAL', trigger_type = 'MANUAL'
-- WHERE execution_type = 'MANUAL_EXECUTION';
--
-- UPDATE sync_history
-- SET sync_type = 'REFRESH', trigger_type = 'API'
-- WHERE execution_type = 'API_REFRESH';
--
-- UPDATE sync_history
-- SET sync_type = 'BATCH', trigger_type = 'SCHEDULED_BATCH'
-- WHERE execution_type = 'SCHEDULED_BATCH';
--
-- UPDATE sync_history
-- SET sync_type = 'INITIAL_LOAD', trigger_type = 'SYSTEM'
-- WHERE execution_type = 'INITIAL_SYSTEM_LOAD';
--
-- UPDATE sync_history
-- SET duration_millis = duration_nanos / 1000000
-- WHERE duration_nanos IS NOT NULL;
--
-- CREATE INDEX idx_sync_type ON sync_history(sync_type);
-- CREATE INDEX idx_trigger_type ON sync_history(trigger_type);
-- DROP INDEX idx_execution_type;
--
-- ALTER TABLE sync_history DROP COLUMN execution_type;
-- ALTER TABLE sync_history DROP COLUMN duration_nanos;
