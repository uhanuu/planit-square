package com.planitsquare.miniservice.adapter.in.startup;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.exception.ExternalApiException;
import com.planitsquare.miniservice.application.port.in.CheckInitialSystemLoadUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
import com.planitsquare.miniservice.application.service.YearPolicy;
import com.planitsquare.miniservice.common.StartUpAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Slf4j
@Profile("!test")
@StartUpAdapter
@RequiredArgsConstructor
public class HolidayBootstrapRunner implements ApplicationRunner {
  private final CheckInitialSystemLoadUseCase checkInitialSystemLoadUseCase;
  private final UploadHolidaysUseCase uploadHolidaysUseCase;

  @Override
  public void run(ApplicationArguments args) {
    if (!checkInitialSystemLoadUseCase.isInitialSystemLoad()) {
      log.info("최초 실행이 아닙니다. 공휴일 업로드를 건너뜁니다.");
      return;
    }

    log.info("최초 실행으로 5 년 × N 개 국가를 일괄 적재 합니다.");

    final UploadHolidayCommand command = new UploadHolidayCommand(
        LocalDate.now().getYear(),
        SyncExecutionType.INITIAL_SYSTEM_LOAD,
        YearPolicy.DEFAULT_RANGE_LENGTH.getValue()
    );

    try {
      uploadHolidaysUseCase.uploadHolidays(command);
      log.info("공휴일 초기 적재 완료");
    } catch (ExternalApiException e) {
      log.error("공휴일 초기 적재 실패 (모든 재시도 실패) - 서비스는 정상적으로 시작됩니다: {}",
          e.getMessage(), e);
    } catch (Exception e) {
      log.error("공휴일 초기 적재 중 예상치 못한 오류 발생 - 서비스는 정상적으로 시작됩니다: {}",
          e.getMessage(), e);
    }
  }
}
