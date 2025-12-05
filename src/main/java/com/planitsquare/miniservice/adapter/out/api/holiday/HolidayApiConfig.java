package com.planitsquare.miniservice.adapter.out.api.holiday;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 공휴일 API 클라이언트 설정.
 *
 * <p>외부 공휴일 API와 통신하기 위한 RestClient 빈을 생성합니다.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(HolidayApiProperties.class)
public class HolidayApiConfig {

  /**
   * 공휴일 API 통신용 RestClient 빈을 생성합니다.
   *
   * <p>타임아웃 및 기본 HTTP 설정을 포함합니다.
   *
   * @param builder RestClient 빌더
   * @return 설정된 RestClient 인스턴스
   */
  @Bean
  public RestClient restClient(RestClient.Builder builder) {
    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
    requestFactory.setReadTimeout(Duration.ofSeconds(10));

    return builder
        .requestFactory(requestFactory)
        .build();
  }
}
