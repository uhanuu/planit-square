package com.planitsquare.miniservice.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 데이터베이스 영속성 계층의 어댑터를 나타내는 어노테이션.
 *
 * <p>헥사고날 아키텍처의 Outbound Adapter로, 데이터베이스 접근을 담당합니다.
 * Port 인터페이스를 구현하며, JPA Repository를 사용하여 도메인 객체를 영속화합니다.
 *
 * @since 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PersistenceAdapter {

    /**
     * Spring Bean의 이름을 지정합니다.
     *
     * @return bean 이름, 또는 빈 문자열
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}