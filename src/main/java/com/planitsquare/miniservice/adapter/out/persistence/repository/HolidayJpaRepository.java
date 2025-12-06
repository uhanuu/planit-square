package com.planitsquare.miniservice.adapter.out.persistence.repository;

import com.planitsquare.miniservice.adapter.out.persistence.entity.HolidayJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayJpaRepository extends JpaRepository<HolidayJpaEntity, Long> {
}
