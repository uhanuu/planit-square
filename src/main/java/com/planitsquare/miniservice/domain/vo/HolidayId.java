package com.planitsquare.miniservice.domain.vo;

/**
 * 휴일의 식별자를 나타내는 Value Object.
 *
 * <p>Holiday Entity의 고유 식별자를 캡슐화합니다.
 *
 * @param value 휴일 ID 값 (양수, null 불가)
 * @since 1.0
 */
public record HolidayId(Long value) {

  /**
   * HolidayId를 생성합니다.
   *
   * @param value 휴일 ID 값
   * @throws IllegalArgumentException ID가 null이거나 양수가 아닌 경우
   */
  public HolidayId {
    if (value == null) {
      throw new IllegalArgumentException("휴일 ID는 null일 수 없습니다.");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("휴일 ID는 양수여야 합니다.");
    }
  }
}
