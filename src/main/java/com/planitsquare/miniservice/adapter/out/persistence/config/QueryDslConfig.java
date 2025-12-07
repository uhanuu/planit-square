package com.planitsquare.miniservice.adapter.out.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정.
 *
 * <p>JPAQueryFactory Bean을 등록합니다.
 *
 * @since 1.0
 */
@Configuration
public class QueryDslConfig {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * JPAQueryFactory Bean을 생성합니다.
   *
   * @return JPAQueryFactory 인스턴스
   */
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }
}
