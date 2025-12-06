package com.planitsquare.miniservice.adapter.out.persistence.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 휴일 메타데이터를 표현하는 Embeddable 객체.
 *
 * <p>휴일의 특성과 속성 정보를 JPA Entity에 임베드하여 사용합니다.
 * types와 applicableRegions는 Holiday Entity에서 @ElementCollection으로 관리합니다.
 *
 * @since 1.0
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayMetadataEmbeddable {

  @Column(name = "fixed")
  private boolean fixed;

  @Column(name = "global")
  private boolean global;

  @Column(name = "launch_year")
  private Integer launchYear;

  /**
   * HolidayMetadataEmbeddable을 생성합니다.
   *
   * @param fixed 고정 휴일 여부
   * @param global 전역 휴일 여부
   * @param launchYear 휴일 시작 연도
   */
  public HolidayMetadataEmbeddable(boolean fixed, boolean global, Integer launchYear) {
    this.fixed = fixed;
    this.global = global;
    this.launchYear = launchYear;
  }
}
