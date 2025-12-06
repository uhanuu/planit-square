package com.planitsquare.miniservice.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CountryJpaEntity 테스트")
class CountryJpaEntityTest {

  @Test
  @DisplayName("CountryJpaEntity가 정상적으로 생성된다")
  void CountryJpaEntity가_정상적으로_생성된다() {
    // Given
    String code = "KR";
    String name = "South Korea";

    // When
    CountryJpaEntity entity = new CountryJpaEntity(code, name);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getCode()).isEqualTo("KR");
    assertThat(entity.getName()).isEqualTo("South Korea");
  }

  @Test
  @DisplayName("국가 코드는 자동으로 대문자로 변환된다")
  void 국가_코드는_자동으로_대문자로_변환된다() {
    // Given
    String code = "kr";
    String name = "South Korea";

    // When
    CountryJpaEntity entity = new CountryJpaEntity(code, name);

    // Then
    assertThat(entity.getCode()).isEqualTo("KR");
  }

  @Test
  @DisplayName("국가 코드가 null이면 예외를 던진다")
  void 국가_코드가_null이면_예외를_던진다() {
    // Given
    String code = null;
    String name = "South Korea";

    // When & Then
    assertThatThrownBy(() -> new CountryJpaEntity(code, name))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 코드가 존재하지 않습니다");
  }

  @Test
  @DisplayName("국가 코드가 빈 문자열이면 예외를 던진다")
  void 국가_코드가_빈_문자열이면_예외를_던진다() {
    // Given
    String code = "   ";
    String name = "South Korea";

    // When & Then
    assertThatThrownBy(() -> new CountryJpaEntity(code, name))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 코드가 존재하지 않습니다");
  }

  @Test
  @DisplayName("국가 이름이 null이면 예외를 던진다")
  void 국가_이름이_null이면_예외를_던진다() {
    // Given
    String code = "KR";
    String name = null;

    // When & Then
    assertThatThrownBy(() -> new CountryJpaEntity(code, name))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 이름이 존재하지 않습니다");
  }

  @Test
  @DisplayName("국가 이름이 빈 문자열이면 예외를 던진다")
  void 국가_이름이_빈_문자열이면_예외를_던진다() {
    // Given
    String code = "KR";
    String name = "   ";

    // When & Then
    assertThatThrownBy(() -> new CountryJpaEntity(code, name))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 이름이 존재하지 않습니다");
  }

  @Test
  @DisplayName("국가 이름을 업데이트한다")
  void 국가_이름을_업데이트한다() {
    // Given
    CountryJpaEntity entity = new CountryJpaEntity("KR", "South Korea");
    String newName = "대한민국";

    // When
    entity.updateName(newName);

    // Then
    assertThat(entity.getName()).isEqualTo("대한민국");
    assertThat(entity.getCode()).isEqualTo("KR"); // 코드는 변경되지 않음
  }

  @Test
  @DisplayName("국가 이름 업데이트 시 null이면 예외를 던진다")
  void 국가_이름_업데이트_시_null이면_예외를_던진다() {
    // Given
    CountryJpaEntity entity = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> entity.updateName(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 이름을 입력해주세요");
  }

  @Test
  @DisplayName("국가 이름 업데이트 시 빈 문자열이면 예외를 던진다")
  void 국가_이름_업데이트_시_빈_문자열이면_예외를_던진다() {
    // Given
    CountryJpaEntity entity = new CountryJpaEntity("KR", "South Korea");

    // When & Then
    assertThatThrownBy(() -> entity.updateName("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("국가 이름을 입력해주세요");
  }
}
