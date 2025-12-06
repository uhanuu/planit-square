package com.planitsquare.miniservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

/**
 * 국가 정보를 저장하는 JPA Entity.
 *
 * <p>도메인 모델과 분리된 영속성 계층의 엔티티입니다.
 * 국가 정보는 마스터 데이터로 관리되며, 여러 Holiday가 참조합니다.
 *
 * @since 1.0
 */
@Entity
@Table(name = "country", indexes = {
    @Index(name = "idx_country_code", columnList = "code")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CountryJpaEntity extends BaseTimeEntity {

  @Id
  @Column(name = "code", nullable = false, unique = true, length = 10)
  private String code;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * CountryJpaEntity를 생성합니다.
   *
   * @param code 국가 코드
   * @param name 국가 이름
   * @throws IllegalArgumentException 국가 코드나 이름이 null이거나 빈 문자열인 경우
   */
  public CountryJpaEntity(String code, String name) {
    Assert.hasText(code, "국가 코드가 존재하지 않습니다.");
    Assert.hasText(name, "국가 이름이 존재하지 않습니다.");
    this.code = code.toUpperCase();
    this.name = name;
  }

  /**
   * 국가 이름을 업데이트합니다.
   *
   * @param name 국가 이름
   * @throws IllegalArgumentException 국가 이름이 null이거나 빈 문자열인 경우
   */
  public void updateName(String name) {
    Assert.hasText(name, "국가 이름을 입력해주세요.");
    this.name = name;
  }
}
