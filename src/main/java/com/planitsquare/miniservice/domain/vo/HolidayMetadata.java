package com.planitsquare.miniservice.domain.vo;

import java.util.Collections;
import java.util.List;

/**
 * 휴일의 메타데이터를 표현하는 Value Object.
 *
 * <p>휴일의 특성과 속성 정보를 불변 객체로 캡슐화합니다.
 *
 * @param fixed 고정 휴일 여부
 * @param global 전역 휴일 여부
 * @param launchYear 휴일 시작 연도 (null 가능)
 * @param types 휴일 타입 목록 (불변)
 * @param applicableRegions 적용 지역 목록 (국가 내 특정 주/도, null 또는 빈 리스트 가능 - 불변)
 * @since 1.0
 */
public record HolidayMetadata(
    boolean fixed,
    boolean global,
    Integer launchYear,
    List<String> types,
    List<String> applicableRegions
) {

  /**
   * HolidayMetadata를 생성합니다.
   *
   * @param fixed 고정 휴일 여부
   * @param global 전역 휴일 여부
   * @param launchYear 휴일 시작 연도 (null 가능)
   * @param types 휴일 타입 목록 (null일 경우 빈 리스트로 초기화)
   */
  public HolidayMetadata {
    types = types == null ? Collections.emptyList() : List.copyOf(types);
    applicableRegions = applicableRegions == null ? Collections.emptyList() : List.copyOf(applicableRegions);
  }

    /**
     * 특정 지역에 적용되는 휴일인지 확인합니다.
     *
     * @return 특정 지역에만 적용되는 휴일이면 true
     */
    public boolean isRegionalHoliday() {
        return !applicableRegions.isEmpty();
    }

    /**
     * 지정된 지역에 이 휴일이 적용되는지 확인합니다.
     *
     * @param region 확인할 지역 이름
     * @return 해당 지역에 적용되거나, 전국 적용 휴일이면 true
     */
    public boolean isApplicableTo(String region) {
        if (region == null || region.isBlank()) {
            return false;
        }
        // 전국 적용 휴일이거나, 해당 지역에 적용되는 경우
        return applicableRegions.isEmpty() || applicableRegions.contains(region.strip());
    }
}
