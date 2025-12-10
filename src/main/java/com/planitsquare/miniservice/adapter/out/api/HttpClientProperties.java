package com.planitsquare.miniservice.adapter.out.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HTTP 클라이언트 설정 Properties.
 *
 * <p>외부 API 호출 시 타임아웃 설정을 정의합니다.
 *
 * @since 1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "http.client")
public class HttpClientProperties {

  /**
   * 연결 타임아웃 (초).
   */
  private int connectTimeoutSeconds = 3;

  /**
   * 읽기 타임아웃 (초).
   */
  private int readTimeoutSeconds = 3;
}
