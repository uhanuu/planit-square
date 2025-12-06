package com.planitsquare.miniservice.adapter.out.api.mapper;

import com.planitsquare.miniservice.adapter.out.api.dto.HolidayResponse;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
import org.springframework.stereotype.Component;

/**
 * HolidayResponse를 Holiday 도메인 모델로 변환하는 Mapper.
 *
 * <p>외부 API 응답 DTO를 도메인 계층의 Entity로 변환합니다.
 * HolidayId는 임시로 생성되며, 실제 영속화 시 데이터베이스에서 할당됩니다.
 *
 * @since 1.0
 */
@Component
public class HolidayResponseMapper {

  /**
   * HolidayResponse를 Holiday 도메인 모델로 변환합니다.
   *
   * @param response 외부 API 응답 DTO
   * @param country 국가 정보
   * @return Holiday 도메인 모델
   * @throws IllegalArgumentException 필수 필드가 누락되었거나 유효하지 않은 경우
   */
  public Holiday toDomain(HolidayResponse response, Country country) {
    // 임시 ID 생성 (영속화 시 실제 ID로 대체됨)
    HolidayId temporaryId = generateTemporaryId(response, country);

    // 메타데이터 생성
    HolidayMetadata metadata = new HolidayMetadata(
        response.getFixed() != null ? response.getFixed() : false,
        response.getGlobal() != null ? response.getGlobal() : false,
        response.getLaunchYear(),
        response.getTypes(),
        response.getCounties()
    );

    return new Holiday(
        temporaryId,
        country,
        response.getLocalName(),
        response.getName(),
        response.getDate(),
        metadata
    );
  }

  /**
   * 임시 HolidayId를 생성합니다.
   *
   * <p>국가 코드와 날짜를 조합하여 고유한 임시 ID를 생성합니다.
   * 실제 영속화 시 데이터베이스의 자동 생성 ID로 대체됩니다.
   *
   * @param response 휴일 응답 DTO
   * @param country 국가 정보
   * @return 임시 HolidayId
   */
  private HolidayId generateTemporaryId(HolidayResponse response, Country country) {
    // 국가 코드와 날짜를 조합한 해시 값을 사용 (충돌 가능성 최소화)
    long hash = (country.getCode() + response.getDate().toString()).hashCode();
    // 양수 보장
    long positiveHash = Math.abs(hash);
    // 0이면 1로 설정
    long id = positiveHash == 0 ? 1L : positiveHash;
    
    return new HolidayId(id);
  }
}
