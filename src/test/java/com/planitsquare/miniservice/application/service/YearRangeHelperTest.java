package com.planitsquare.miniservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("YearRangeHelper 테스트")
class YearRangeHelperTest {

  @Test
  @DisplayName("끝 연도를 입력하면 최근 5년 목록을 반환한다")
  void 끝_연도를_입력하면_최근_5년_목록을_반환한다() {
    // Given
    int endYear = 2025;

    // When
    List<Integer> years = YearRangeHelper.generateYearsFromEnd(endYear);

    // Then
    assertThat(years).containsExactly(2021, 2022, 2023, 2024, 2025);
  }

  @Test
  @DisplayName("끝 연도가 최소 연도 미만이면 예외를 던진다")
  void 끝_연도가_최소_연도_미만이면_예외를_던진다() {
    // Given
    int endYear = 1999;

    // When & Then
    assertThatThrownBy(() -> YearRangeHelper.generateYearsFromEnd(endYear))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("지원되지 않는 연도입니다");
  }

  @Test
  @DisplayName("계산된 시작 연도가 최소 연도 미만이면 예외를 던진다")
  void 계산된_시작_연도가_최소_연도_미만이면_예외를_던진다() {
    // Given
    int endYear = 2003; // 시작 연도 계산: 2003 - 5 + 1 = 1999 (MIN_YEAR 2000 미만)

    // When & Then
    assertThatThrownBy(() -> YearRangeHelper.generateYearsFromEnd(endYear))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("지원되지 않는 연도입니다");
  }

  @Test
  @DisplayName("경계값: 시작 연도가 정확히 최소 연도인 경우")
  void 경계값_시작_연도가_정확히_최소_연도인_경우() {
    // Given
    int endYear = 2004; // 시작 연도 계산: 2004 - 5 + 1 = 2000 (MIN_YEAR와 동일)

    // When
    List<Integer> years = YearRangeHelper.generateYearsFromEnd(endYear);

    // Then
    assertThat(years).containsExactly(2000, 2001, 2002, 2003, 2004);
  }
}
