package com.planitsquare.miniservice.domain.model;

import com.planitsquare.miniservice.domain.vo.Country;
import com.planitsquare.miniservice.domain.vo.HolidayId;
import com.planitsquare.miniservice.domain.vo.HolidayMetadata;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 휴일 정보를 나타내는 Aggregate Root Entity.
 *
 * <p>국가별 휴일의 기본 정보, 메타데이터, 적용 지역을 캡슐화합니다.
 * Rich Domain Model로 비즈니스 로직을 포함합니다.
 *
 * @since 1.0
 */
@Getter
public class Holiday {

    private final HolidayId id;
    private final Country country;
    private final String localName;
    private final String name;
    private final LocalDate date;
    private final HolidayMetadata metadata;

    /**
     * Holiday를 생성합니다.
     *
     * @param id                휴일 식별자
     * @param country           국가 정보
     * @param localName         현지 이름
     * @param name              영문 이름
     * @param date              휴일 날짜
     * @param metadata          휴일 메타데이터
     * @throws IllegalArgumentException 필수 필드가 null이거나 이름이 빈 문자열인 경우
     */
    public Holiday(
            HolidayId id,
            Country country,
            String localName,
            String name,
            LocalDate date,
            HolidayMetadata metadata
    ) {
        validateFields(id, country, localName, name, date, metadata);

        this.id = id;
        this.country = country;
        this.localName = localName.strip();
        this.name = name.strip();
        this.date = date;
        this.metadata = metadata;
    }

    private void validateFields(
            HolidayId id,
            Country country,
            String localName,
            String name,
            LocalDate date,
            HolidayMetadata metadata) {
        if (id == null) {
            throw new IllegalArgumentException("휴일 ID가 존재하지 않습니다.");
        }
        if (country == null) {
            throw new IllegalArgumentException("국가 정보가 존재하지 않습니다.");
        }
        if (localName == null || localName.isBlank()) {
            throw new IllegalArgumentException("현지 이름이 존재하지 않습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("영문 이름이 존재하지 않습니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("휴일 날짜가 존재하지 않습니다.");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("휴일 메타데이터가 존재하지 않습니다.");
        }
    }

    /**
     * 특정 날짜가 이 휴일에 해당하는지 확인합니다.
     *
     * @param targetDate 확인할 날짜
     * @return 해당 날짜가 이 휴일이면 true
     */
    public boolean isOn(LocalDate targetDate) {
        return date.equals(targetDate);
    }


    /**
     * 특정 지역에 적용되는 휴일인지 확인합니다.
     *
     * @return 특정 지역에만 적용되는 휴일이면 true
     */
    public boolean isRegionalHoliday() {
        return metadata.isRegionalHoliday();
    }

    /**
     * 지정된 지역에 이 휴일이 적용되는지 확인합니다.
     *
     * @param region 확인할 지역 이름
     * @return 해당 지역에 적용되거나, 전국 적용 휴일이면 true
     */
    public boolean isApplicableTo(String region) {
        return metadata.isApplicableTo(region);
    }

    /**
     * 동일한 국가의 휴일인지 확인합니다.
     *
     * @param targetCountry 확인할 국가
     * @return 동일한 국가이면 true
     */
    public boolean isSameCountry(Country targetCountry) {
        return country.equals(targetCountry);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Holiday holiday = (Holiday) o;
        return Objects.equals(id, holiday.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", country=" + country +
                ", localName='" + localName + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", metadata=" + metadata +
                '}';
    }
}

