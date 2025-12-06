package com.planitsquare.miniservice.adapter.out.persistence.entity;

import com.planitsquare.miniservice.adapter.out.persistence.vo.HolidayMetadataEmbeddable;
import com.planitsquare.miniservice.domain.vo.HolidayType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 휴일 정보를 저장하는 JPA Entity.
 *
 * <p>도메인 모델과 분리된 영속성 계층의 엔티티입니다.
 * Country는 별도 테이블로 관리하며 ManyToOne 관계를 유지합니다.
 *
 * @since 1.0
 */
@Entity
@Table(name = "holiday", indexes = {
    @Index(name = "idx_holiday_date", columnList = "date"),
    @Index(name = "idx_holiday_country", columnList = "country_code")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "holiday_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "country_code", referencedColumnName = "code", nullable = false)
  private CountryJpaEntity country;

  @Column(name = "local_name", nullable = false, length = 100)
  private String localName;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Embedded
  private HolidayMetadataEmbeddable metadata;

  @ElementCollection(fetch = FetchType.LAZY)
  @Enumerated(EnumType.STRING)
  @CollectionTable(
      name = "holiday_types",
      joinColumns = @JoinColumn(name = "holiday_id")
  )
  @Column(name = "type", length = 50)
  private List<HolidayType> types = new ArrayList<>();

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "holiday_applicable_regions",
      joinColumns = @JoinColumn(name = "holiday_id")
  )
  @Column(name = "region", length = 100)
  private List<String> applicableRegions = new ArrayList<>();

  /**
   * HolidayJpaEntity를 생성합니다.
   *
   * @param country           국가 엔티티
   * @param localName         현지 이름
   * @param name              영문 이름
   * @param date              휴일 날짜
   * @param metadata          휴일 메타데이터
   * @param types             휴일 타입 목록
   * @param applicableRegions 적용 지역 목록
   */
  public HolidayJpaEntity(
      CountryJpaEntity country,
      String localName,
      String name,
      LocalDate date,
      HolidayMetadataEmbeddable metadata,
      List<HolidayType> types,
      List<String> applicableRegions
  ) {
    this.country = country;
    this.localName = localName;
    this.name = name;
    this.date = date;
    this.metadata = metadata;
    this.types = Objects.requireNonNullElse(types, Collections.emptyList());
    this.applicableRegions = Objects.requireNonNullElse(applicableRegions, Collections.emptyList());
  }

  /**
   * 휴일 정보를 업데이트합니다.
   *
   * @param localName         현지 이름
   * @param name              영문 이름
   * @param date              휴일 날짜
   * @param metadata          메타데이터
   * @param types             휴일 타입
   * @param applicableRegions 적용 지역
   */
  public void update(
      String localName,
      String name,
      LocalDate date,
      HolidayMetadataEmbeddable metadata,
      List<HolidayType> types,
      List<String> applicableRegions
  ) {
    this.localName = localName;
    this.name = name;
    this.date = date;
    this.metadata = metadata;
    this.types = Objects.requireNonNullElse(types, Collections.emptyList());
    this.applicableRegions = Objects.requireNonNullElse(applicableRegions, Collections.emptyList());
  }
}
