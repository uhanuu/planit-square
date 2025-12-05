package com.planitsquare.miniservice.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("HolidayId Value Object 테스트")
class HolidayIdTest {

  @Test
  @DisplayName("유효한 ID로 HolidayId를 생성한다")
  void 유효한_ID로_HolidayId를_생성한다() {
    // Given
    Long id = 1L;

    // When
    HolidayId holidayId = new HolidayId(id);

    // Then
    assertThat(holidayId.value()).isEqualTo(1L);
  }

  @Test
  @DisplayName("null ID로 생성 시 예외를 던진다")
  void null_ID로_생성_시_예외를_던진다() {
    // Given
    Long id = null;

    // When & Then
    assertThatThrownBy(() -> new HolidayId(id))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("휴일 ID는 null일 수 없습니다.");
  }

  @ParameterizedTest(name = "id값 : {0}")
  @ValueSource(longs = {0, -1, -28})
  @DisplayName("음수 ID로 생성 시 예외를 던진다")
  void ID는_0미만_일_수_없다(Long id) {
    // When & Then
    assertThatThrownBy(() -> new HolidayId(id))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("휴일 ID는 양수여야 합니다.");
  }

  @Test
  @DisplayName("동일한 값을 가진 HolidayId는 동일하다")
  void 동일한_값을_가진_HolidayId는_동일하다() {
    // Given
    HolidayId id1 = new HolidayId(1L);
    HolidayId id2 = new HolidayId(1L);

    // When & Then
    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }

  @Test
  @DisplayName("다른 값을 가진 HolidayId는 다르다")
  void 다른_값을_가진_HolidayId는_다르다() {
    // Given
    HolidayId id1 = new HolidayId(1L);
    HolidayId id2 = new HolidayId(2L);

    // When & Then
    assertThat(id1).isNotEqualTo(id2);
  }
}
