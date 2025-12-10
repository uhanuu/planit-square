package com.planitsquare.miniservice.application.port.in;


import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;

/**
 * 공휴일 업로드 커맨드.
 *
 * <p>공휴일 데이터를 업로드하거나 동기화할 때 필요한 정보를 담는 불변 커맨드 객체입니다.
 *
 * @param year 끝 연도 (이 연도를 포함하여 이전 연도들의 공휴일을 조회)
 * @param executionType 실행 타입 (INITIAL_SYSTEM_LOAD, MANUAL_EXECUTION, SCHEDULED_BATCH 등)
 * @param yearRangeLength 조회할 연도 범위 길이 (끝 연도를 포함하여 몇 년치를 조회할지 결정)
 * @since 1.0
 */
public record UploadHolidayCommand(
    int year,
    SyncExecutionType executionType,
    int yearRangeLength
) {
}
