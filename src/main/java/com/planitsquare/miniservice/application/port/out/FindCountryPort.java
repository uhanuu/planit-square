package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.vo.Country;

import java.util.List;
import java.util.Optional;

public interface FindCountryPort {
  List<Country> findAll();
  boolean existsByCode(String code);
  Optional<Country> findByCode(String code);
}
