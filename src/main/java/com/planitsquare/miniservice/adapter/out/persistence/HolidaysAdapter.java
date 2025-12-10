package com.planitsquare.miniservice.adapter.out.persistence;

import com.planitsquare.miniservice.adapter.out.persistence.entity.CountryJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import com.planitsquare.miniservice.adapter.out.persistence.mapper.CountryMapper;
import com.planitsquare.miniservice.adapter.out.persistence.mapper.HolidayMapper;
import com.planitsquare.miniservice.adapter.out.persistence.repository.HolidayJpaRepository;
import com.planitsquare.miniservice.application.port.out.DeleteHolidaysPort;
import com.planitsquare.miniservice.application.port.out.SaveAllHolidaysPort;
import com.planitsquare.miniservice.common.PersistenceAdapter;
import com.planitsquare.miniservice.domain.model.Holiday;
import com.planitsquare.miniservice.domain.vo.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
@Transactional
public class HolidaysAdapter implements SaveAllHolidaysPort, DeleteHolidaysPort {
  private final HolidayJpaRepository holidayJpaRepository;
  private final HolidayMapper holidayMapper;
  private final CountryMapper countryMapper;

  @Override
  public void saveAllHolidays(List<Holiday> holidays) {
    final List<HolidayJpaEntity> holidayJpaEntities = holidays.stream()
        .map(this::toEntity)
        .toList();

    holidayJpaRepository.saveAll(holidayJpaEntities);
  }

  @Override
  public int deleteByYearAndCountryCode(int year, CountryCode countryCode) {
    return holidayJpaRepository.deleteByCountryCodeAndYear(countryCode.code(), year);
  }

  @Override
  public int deleteByYear(List<Integer> years) {
    return holidayJpaRepository.deleteByYear(years);
  }

  private HolidayJpaEntity toEntity(Holiday holiday) {
    CountryJpaEntity countryJpaEntity = countryMapper.toEntity(holiday.getCountry());
    return holidayMapper.toEntity(holiday, countryJpaEntity);
  }
}
