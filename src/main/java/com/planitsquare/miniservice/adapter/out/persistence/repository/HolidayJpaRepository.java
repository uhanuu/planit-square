package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HolidayJpaRepository extends JpaRepository<HolidayJpaEntity, Long> {

  /**
   * 특정 연도와 국가 코드에 해당하는 모든 공휴일을 삭제합니다.
   *
   * @param countryCode 국가 코드
   * @param year 연도
   * @return 삭제된 레코드 수
   */
  @Modifying
  @Query("DELETE FROM HolidayJpaEntity h WHERE h.country.code = :countryCode AND YEAR(h.date) = :year")
  int deleteByCountryCodeAndYear(@Param("countryCode") String countryCode, @Param("year") int year);

  /**
   * 특정 연도의 해당하는 모든 공휴일을 삭제합니다.
   *
   * @param years 연도 목록
   * @return 삭제된 레코드 수
   */
  @Modifying
  @Query("DELETE FROM HolidayJpaEntity h WHERE YEAR(h.date) in :years")
  int deleteByYear(@Param("years") List<Integer> years);
}
