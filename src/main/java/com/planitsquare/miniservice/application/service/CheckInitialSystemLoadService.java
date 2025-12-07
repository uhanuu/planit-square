package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.application.port.in.CheckInitialSystemLoadUseCase;
import com.planitsquare.miniservice.application.port.out.SyncJobPort;
import com.planitsquare.miniservice.common.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CheckInitialSystemLoadService implements CheckInitialSystemLoadUseCase {
  private final SyncJobPort syncJobPort;

  @Override
  public boolean isInitialSystemLoad() {
    return syncJobPort.isInitialSystemLoad();
  }
}
