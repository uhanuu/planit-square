package com.planitsquare.miniservice.application.port.in;


import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;

public record UploadHolidayCommand(
    int year,
    SyncExecutionType executionType
) {
}
