package com.planitsquare.miniservice.adapter.in.web.dto.response;

import com.planitsquare.miniservice.domain.model.Holiday;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 공휴일 응답 DTO.
 *
 * <p>REST API 응답으로 전달되는 공휴일 정보입니다.
 *
 * @since 1.0
 */
@Getter
@Builder
public class HolidayResponse {

  /**
   * 공휴일 ID.
   */
  private final Long id;

  /**
   * 국가 코드.
   */
  private final String countryCode;

  /**
   * 국가 이름.
   */
  private final String countryName;

  /**
   * 현지 이름.
   */
  private final String localName;

  /**
   * 영문 이름.
   */
  private final String name;

  /**
   * 공휴일 날짜.
   */
  private final LocalDate date;

  /**
   * 고정 휴일 여부.
   */
  private final boolean fixed;

  /**
   * 전역 휴일 여부.
   */
  private final boolean global;

  /**
   * 휴일 시작 연도.
   */
  private final Integer launchYear;

  /**
   * 휴일 타입 목록.
   */
  private final List<String> types;

  /**
   * 적용 지역 목록.
   */
  private final List<String> applicableRegions;

  /**
   * Holiday 도메인 객체로부터 HolidayResponseDto를 생성합니다.
   *
   * @param holiday Holiday 도메인 객체
   * @return HolidayResponseDto
   */
  public static HolidayResponse from(Holiday holiday) {
    return HolidayResponse.builder()
        .id(holiday.getId().value())
        .countryCode(holiday.getCountry().getCode())
        .countryName(holiday.getCountry().getName())
        .localName(holiday.getLocalName())
        .name(holiday.getName())
        .date(holiday.getDate())
        .fixed(holiday.getMetadata().fixed())
        .global(holiday.getMetadata().global())
        .launchYear(holiday.getMetadata().launchYear())
        .types(holiday.getMetadata().types())
        .applicableRegions(holiday.getMetadata().applicableRegions())
        .build();
  }
}
