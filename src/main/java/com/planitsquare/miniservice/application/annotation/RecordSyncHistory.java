package com.planitsquare.miniservice.application.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 동기화 이력을 자동으로 기록하는 어노테이션.
 *
 * <p>이 어노테이션이 적용된 메서드는 AOP를 통해 성공/실패 이력이 자동으로 기록됩니다.
 * 메서드 실행 시작 시간과 종료 시간을 측정하여 소요 시간을 계산하고,
 * 성공 시 {@code recordSuccess}, 실패 시 {@code recordFailure}를 자동으로 호출합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * @RecordSyncHistory(
 *   country = "#country",
 *   year = "#year"
 * )
 * private void fetchAndSaveHolidaysForCountryAndYear(
 *     Country country, int year) {
 *   // 성공/실패 기록은 AOP가 자동 처리
 *   // 비즈니스 로직만 작성
 * }
 * }</pre>
 *
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordSyncHistory {

  /**
   * 국가를 나타내는 SpEL 표현식.
   *
   * <p>메서드 파라미터를 참조하여 국가를 추출할 수 있습니다.
   * 예: {@code "#country"}
   *
   * @return SpEL 표현식
   */
  String country();

  /**
   * 연도를 나타내는 SpEL 표현식.
   *
   * <p>메서드 파라미터를 참조하여 연도를 추출할 수 있습니다.
   * 예: {@code "#year"}
   *
   * @return SpEL 표현식
   */
  String year();
}
