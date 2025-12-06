package com.planitsquare.miniservice.application.service;

import com.planitsquare.miniservice.domain.vo.Country;

/**
 * 공휴일 동기화 커맨드.
 *
 * <p>특정 국가와 연도에 대한 공휴일 동기화 작업을 수행하기 위한 커맨드입니다.
 * Job ID를 포함하여 동기화 이력 추적을 가능하게 합니다.
 *
 * @param jobId Job ID
 * @param country 국가
 * @param year 연도
 * @since 1.0
 */
public record SyncHolidayCommand(
    Long jobId,
    Country country,
    int year
) {
}
