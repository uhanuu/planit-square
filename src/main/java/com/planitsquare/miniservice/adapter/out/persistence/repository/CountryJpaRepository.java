package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 국가 정보 JPA Repository.
 *
 * <p>데이터베이스의 국가 테이블에 접근하는 Repository입니다.
 *
 * @since 1.0
 */
public interface CountryJpaRepository extends JpaRepository<CountryJpaEntity, String> {
}
