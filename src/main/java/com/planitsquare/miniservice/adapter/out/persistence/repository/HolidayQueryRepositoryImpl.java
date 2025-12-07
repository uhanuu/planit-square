package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.QHolidayJpaEntity;
import com.planitsquare.miniservice.application.port.in.SearchHolidaysQuery;
import com.planitsquare.miniservice.domain.vo.HolidayType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Holiday 동적 쿼리 Repository 구현체.
 *
 * <p>QueryDSL을 사용하여 다양한 검색 조건을 동적으로 처리합니다.
 *
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
public class HolidayQueryRepositoryImpl implements HolidayQueryRepository {

  private final JPAQueryFactory queryFactory;
  private static final QHolidayJpaEntity holiday = QHolidayJpaEntity.holidayJpaEntity;

  @Override
  public Page<HolidayJpaEntity> search(SearchHolidaysQuery query) {
    Pageable pageable = query.getPageable();

    JPAQuery<HolidayJpaEntity> jpaQuery = queryFactory
        .selectFrom(holiday)
        .leftJoin(holiday.country).fetchJoin()
        .where(
            yearEq(query.getYear()),
            countryCodeEq(query.getCountryCode()),
            dateBetween(query.getFrom(), query.getTo()),
            typeContains(query.getType()),
            nameLike(query.getName())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    // 정렬 적용
    OrderSpecifier<?>[] orderSpecifiers = createOrderSpecifiers(pageable.getSort());
    if (orderSpecifiers.length > 0) {
      jpaQuery.orderBy(orderSpecifiers);
    } else {
      jpaQuery.orderBy(holiday.date.asc()); // 기본 정렬
    }

    List<HolidayJpaEntity> content = jpaQuery.fetch();

    // 전체 개수 조회
    Long total = queryFactory
        .select(holiday.count())
        .from(holiday)
        .where(
            yearEq(query.getYear()),
            countryCodeEq(query.getCountryCode()),
            dateBetween(query.getFrom(), query.getTo()),
            typeContains(query.getType()),
            nameLike(query.getName())
        )
        .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0L);
  }

  private BooleanExpression yearEq(Integer year) {
    return year != null ? holiday.date.year().eq(year) : null;
  }

  private BooleanExpression countryCodeEq(String countryCode) {
    return countryCode != null ? holiday.country.code.eq(countryCode.toUpperCase()) : null;
  }

  private BooleanExpression dateBetween(LocalDate from, LocalDate to) {
    if (from != null && to != null) {
      return holiday.date.between(from, to);
    }
    if (from != null) {
      return holiday.date.goe(from);
    }
    if (to != null) {
      return holiday.date.loe(to);
    }
    return null;
  }

  /**
   * 타입 조건.
   */
  private BooleanExpression typeContains(String type) {
    if (type == null || type.isBlank()) {
      return null;
    }
    try {
      HolidayType holidayType = HolidayType.fromString(type);
      return holiday.types.contains(holidayType);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * 이름 검색 조건 (부분 일치).
   */
  private BooleanExpression nameLike(String name) {
    if (name != null && !name.isBlank()) {
      return holiday.name.containsIgnoreCase(name).or(holiday.localName.containsIgnoreCase(name));
    }
    return null;
  }

  /**
   * 정렬 조건 생성.
   */
  private OrderSpecifier<?>[] createOrderSpecifiers(Sort sort) {
    if (sort == null || sort.isUnsorted()) {
      return new OrderSpecifier[0];
    }

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    for (Sort.Order order : sort) {
      String field = order.getProperty();
      boolean isAsc = order.isAscending();

      switch (field.toLowerCase()) {
        case "date":
          orderSpecifiers.add(isAsc ? holiday.date.asc() : holiday.date.desc());
          break;
        case "name":
          orderSpecifiers.add(isAsc ? holiday.name.asc() : holiday.name.desc());
          break;
        case "country":
          orderSpecifiers.add(isAsc ? holiday.country.code.asc() : holiday.country.code.desc());
          break;
        default:
          // 알 수 없는 필드는 무시
          break;
      }
    }

    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }
}
