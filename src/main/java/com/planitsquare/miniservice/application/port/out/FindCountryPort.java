package com.planitsquare.miniservice.application.port.out;

import com.planitsquare.miniservice.domain.vo.Country;

import java.util.List;

public interface FindCountryPort {
  List<Country> findAll();
}
