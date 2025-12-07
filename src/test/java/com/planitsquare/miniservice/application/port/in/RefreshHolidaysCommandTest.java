package com.planitsquare.miniservice.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RefreshHolidaysCommand 테스트")
class RefreshHolidaysCommandTest {

  @Nested
  @DisplayName("생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("유효한 파라미터로 커맨드를 생성한다")
    void 유효한_파라미터로_커맨드를_생성한다() {
      // Given
      Integer year = 2024;
      CountryCode countryCode = new CountryCode("KR");
      SyncExecutionType executionType = SyncExecutionType.API_REFRESH;

      // When & Then
      assertThatCode(() -> new RefreshHolidaysCommand(year, countryCode, executionType))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("생성된 커맨드는 전달받은 값을 가진다")
    void 생성된_커맨드는_전달받은_값을_가진다() {
      // Given
      Integer year = 2024;
      CountryCode countryCode = new CountryCode("KR");
      SyncExecutionType executionType = SyncExecutionType.API_REFRESH;

      // When
      RefreshHolidaysCommand command = new RefreshHolidaysCommand(year, countryCode, executionType);

      // Then
      assertThat(command.year()).isEqualTo(year);
      assertThat(command.countryCode()).isEqualTo(countryCode);
      assertThat(command.executionType()).isEqualTo(executionType);
    }
  }

  @Nested
  @DisplayName("검증 테스트")
  class ValidationTest {

    @Test
    @DisplayName("year가 null이면 예외를 던진다")
    void year가_null이면_예외를_던진다() {
      // Given
      Integer year = null;
      CountryCode countryCode = new CountryCode("KR");
      SyncExecutionType executionType = SyncExecutionType.API_REFRESH;

      // When & Then
      assertThatThrownBy(() -> new RefreshHolidaysCommand(year, countryCode, executionType))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("연도가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("countryCode가 null이면 예외를 던진다")
    void countryCode가_null이면_예외를_던진다() {
      // Given
      Integer year = 2024;
      CountryCode countryCode = null;
      SyncExecutionType executionType = SyncExecutionType.API_REFRESH;

      // When & Then
      assertThatThrownBy(() -> new RefreshHolidaysCommand(year, countryCode, executionType))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("국가 코드가 존재하지 않습니다.");
    }
  }
}
