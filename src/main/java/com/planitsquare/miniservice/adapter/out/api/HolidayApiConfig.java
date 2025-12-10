package com.planitsquare.miniservice.adapter.out.api;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 공휴일 API 클라이언트 설정.
 *
 * <p>외부 공휴일 API와 통신하기 위한 RestClient 빈을 생성합니다.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({HolidayApiProperties.class, HttpClientProperties.class})
@RequiredArgsConstructor
public class HolidayApiConfig {

  private final HttpClientProperties httpClientProperties;

  /**
   * 공휴일 API 통신용 RestClient 빈을 생성합니다.
   *
   * <p>타임아웃 설정:
   * <ul>
   *   <li>연결 타임아웃: {@link HttpClientProperties#getConnectTimeoutSeconds()}</li>
   *   <li>읽기 타임아웃: {@link HttpClientProperties#getReadTimeoutSeconds()}</li>
   * </ul>
   *
   * @param builder RestClient 빌더
   * @return 설정된 RestClient 인스턴스
   * @since 1.0
   */
  @Bean
  public RestClient restClient(RestClient.Builder builder) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(httpClientProperties.getConnectTimeoutSeconds()));
    requestFactory.setReadTimeout(Duration.ofSeconds(httpClientProperties.getReadTimeoutSeconds()));

    return builder
        .requestFactory(requestFactory)
        .build();
  }
}

