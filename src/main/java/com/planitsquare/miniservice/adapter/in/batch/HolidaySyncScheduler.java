package com.planitsquare.miniservice.adapter.in.batch;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.SyncHolidayDataUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * 공휴일 자동 동기화 스케줄러.
 *
 * <p>매년 1월 2일 01:00 KST에 전년도와 금년도의 모든 국가 공휴일 데이터를 자동으로 동기화합니다.
 * 기존 데이터를 삭제하고 외부 API로부터 최신 공휴일 데이터를 가져와 저장합니다.
 *
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HolidaySyncScheduler {
  private static final int YEAR_RANGE_LENGTH = 2; // 작년, 올해
  private final SyncHolidayDataUseCase syncHolidayDataUseCase;

  /**
   * 매년 1월 2일 01:00 KST에 전년도와 금년도 공휴일 데이터를 동기화합니다.
   *
   * <p>스케줄러는 다음 작업을 수행합니다:
   * <ul>
   *   <li>현재 연도 기준으로 전년도와 금년도를 계산</li>
   *   <li>해당 연도의 기존 공휴일 데이터 삭제</li>
   *   <li>외부 API로부터 모든 국가의 최신 공휴일 데이터 조회</li>
   *   <li>조회된 데이터를 데이터베이스에 저장</li>
   * </ul>
   *
   * <p>Cron 표현식: {@code "0 0 1 2 1 ?"} (매년 1월 2일 01:00)
   * <p>시간대: Asia/Seoul (KST)
   *
   * @since 1.0
   */
  @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
  public void runHolidaySyncJob() {
    log.info("=== 공휴일 자동 동기화 스케줄러 시작 ===");
    int currentYear = LocalDate.now(ZoneId.of("Asia/Seoul")).getYear();
    log.info("현재 연도: {}, 동기화 대상 연도 범위: {} ~ {}",
        currentYear, currentYear - YEAR_RANGE_LENGTH + 1, currentYear);

    final UploadHolidayCommand command = new UploadHolidayCommand(
        currentYear,
        SyncExecutionType.SCHEDULED_BATCH,
        YEAR_RANGE_LENGTH
    );

    syncHolidayDataUseCase.syncAnnualHolidays(command);
    log.info("=== 공휴일 자동 동기화 스케줄러 완료 ===");
  }
}
