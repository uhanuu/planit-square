package com.planitsquare.miniservice.adapter.out.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Retry 설정 Properties.
 *
 * <p>외부 API 호출 재시도 정책을 정의합니다.
 *
 * @since 1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "retry")
public class RetryProperties {

  /**
   * 최대 재시도 횟수 (첫 시도 포함).
   */
  private int maxAttempts = 3;

  /**
   * 초기 대기 시간 (밀리초).
   */
  private long initialIntervalMs = 500;

  /**
   * 지수 백오프 배율.
   */
  private double multiplier = 2.0;
}
