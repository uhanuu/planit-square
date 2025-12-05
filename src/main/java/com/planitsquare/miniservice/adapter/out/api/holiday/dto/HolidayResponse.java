package com.planitsquare.miniservice.adapter.out.api.holiday.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

/**
 * 공휴일 정보 응답 DTO.
 *
 * <p>외부 API로부터 받은 공휴일 정보를 담는 데이터 전송 객체입니다.
 *
 * @since 1.0
 */
@Getter
public class HolidayResponse {

  /**
   * 공휴일 날짜.
   */
  private LocalDate date;

  /**
   * 현지 언어로 된 공휴일 명칭.
   *
   * <p>예시: {@code 설날}, {@code Thanksgiving Day}
   */
  private String localName;

  /**
   * 영문 공휴일 명칭.
   *
   * <p>예시: {@code New Year's Day}, {@code Christmas Day}
   */
  private String name;

  /**
   * 고정 날짜 여부.
   *
   * <p>매년 같은 날짜에 발생하는지 여부
   */
  private Boolean fixed;

  /**
   * 전국 공휴일 여부.
   *
   * <p>{@code true}인 경우 전국 단위 공휴일, {@code false}인 경우 지역 공휴일
   */
  private Boolean global;

  /**
   * 해당 공휴일이 적용되는 특정 지역 목록.
   *
   * <p>{@code null}인 경우 전국 적용
   */
  private List<String> counties;

  /**
   * 공휴일이 시작된 연도.
   */
  private Integer launchYear;

  /**
   * 공휴일 유형 목록.
   *
   * <p>예시: {@code [Public]}, {@code [Bank, Public]}
   */
  private List<String> types;
}
