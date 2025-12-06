package com.planitsquare.miniservice.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 외부 API 연동 어댑터를 나타내는 어노테이션.
 *
 * <p>헥사고날 아키텍처의 Outbound Adapter로, 외부 시스템(REST API, gRPC 등)과의
 * 통신을 담당합니다. Port 인터페이스를 구현하며, RestClient 등을 사용하여
 * 외부 API를 호출합니다.
 *
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ExternalApiAdapter {
  /**
   * Spring Bean의 이름을 지정합니다.
   *
   * @return bean 이름
   */
  @AliasFor(annotation = Component.class)
  String value() default "";
}
