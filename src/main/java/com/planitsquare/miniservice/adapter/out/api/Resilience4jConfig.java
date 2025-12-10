package com.planitsquare.miniservice.adapter.out.api;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;

/**
 * Resilience4j Retry 설정.
 *
 * <p>외부 API 호출 실패 시 재시도 정책을 정의합니다.
 * 지수 백오프를 사용하여 재시도 간격을 점진적으로 증가시킵니다.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(RetryProperties.class)
@RequiredArgsConstructor
public class Resilience4jConfig {

  private final RetryProperties retryProperties;

  /**
   * RetryRegistry Bean을 생성합니다.
   *
   * <p>재시도 정책:
   * <ul>
   *   <li>최대 재시도 횟수: {@link RetryProperties#getMaxAttempts()}</li>
   *   <li>초기 대기 시간: {@link RetryProperties#getInitialIntervalMs()}</li>
   *   <li>지수 백오프 배율: {@link RetryProperties#getMultiplier()}</li>
   * </ul>
   *
   * <p>재시도 대상 예외:
   * <ul>
   *   <li>{@link RestClientException}: REST 클라이언트 오류</li>
   *   <li>{@link ConnectException}: 연결 실패</li>
   *   <li>{@link SocketTimeoutException}: 소켓 타임아웃</li>
   *   <li>{@link IOException}: 일반 I/O 오류</li>
   * </ul>
   *
   * <p>재시도하지 않는 예외:
   * <ul>
   *   <li>{@link HttpClientErrorException}: 4xx 클라이언트 오류 (재시도 불필요)</li>
   * </ul>
   *
   * @return 설정된 RetryRegistry
   * @since 1.0
   */
  @Bean
  public RetryRegistry retryRegistry() {
    IntervalFunction backoff = IntervalFunction.ofExponentialBackoff(
        Duration.ofMillis(retryProperties.getInitialIntervalMs()),
        retryProperties.getMultiplier()
    );

    RetryConfig config = RetryConfig.custom()
        .maxAttempts(retryProperties.getMaxAttempts())
        .intervalFunction(backoff)
        .retryExceptions(
            RestClientException.class,
            ConnectException.class,
            SocketTimeoutException.class,
            IOException.class
        )
        .ignoreExceptions(
            HttpClientErrorException.class
        )
        .build();

    return RetryRegistry.of(config);
  }

  /**
   * holidayApi용 Retry Bean을 생성합니다.
   *
   * @param retryRegistry RetryRegistry
   * @return holidayApi Retry 인스턴스
   * @since 1.0
   */
  @Bean
  public Retry holidayApiRetry(RetryRegistry retryRegistry) {
    return retryRegistry.retry("holidayApi");
  }
}

