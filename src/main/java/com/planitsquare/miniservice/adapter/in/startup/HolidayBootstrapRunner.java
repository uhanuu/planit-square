package com.planitsquare.miniservice.adapter.in.startup;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.port.in.CheckInitialSystemLoadUseCase;
import com.planitsquare.miniservice.application.port.in.UploadHolidayCommand;
import com.planitsquare.miniservice.application.port.in.UploadHolidaysUseCase;
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
    if (checkInitialSystemLoadUseCase.isInitialSystemLoad()) {
      log.info("최초 실행으로 5 년 × N 개 국가를 일괄 적재 합니다.");
      final UploadHolidayCommand command = new UploadHolidayCommand(
          LocalDate.now().getYear(),
          SyncExecutionType.INITIAL_SYSTEM_LOAD
      );

      uploadHolidaysUseCase.uploadHolidays(command);
    }
  }
}
