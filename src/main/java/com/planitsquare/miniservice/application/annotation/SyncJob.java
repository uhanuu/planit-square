package com.planitsquare.miniservice.application.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 동기화 Job의 생명주기를 자동으로 관리하는 어노테이션.
 *
 * <p>이 어노테이션이 적용된 메서드는 AOP를 통해 Job 시작 및 완료가 자동으로 처리됩니다.
 * Job ID는 메서드 실행 전에 생성되어 ThreadLocal에 저장되고, 메서드 실행 후 자동으로 완료 처리됩니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * @SyncJob(executionType = "#command.executionType()")
 * public void uploadHolidays(UploadHolidayCommand command) {
 *   // Job 시작/완료는 AOP가 자동 처리
 *   // 비즈니스 로직만 작성
 * }
 * }</pre>
 *
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SyncJob {

  /**
   * 실행 타입을 나타내는 SpEL 표현식.
   *
   * <p>메서드 파라미터를 참조하여 실행 타입을 추출할 수 있습니다.
   * 예: {@code "#command.executionType()"}
   *
   * @return SpEL 표현식
   */
  String executionType();
}
