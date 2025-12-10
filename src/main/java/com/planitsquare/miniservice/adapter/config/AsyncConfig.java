package com.planitsquare.miniservice.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정.
 *
 * <p>Spring의 @Async 어노테이션을 사용한 비동기 처리를 활성화하고,
 * 스레드 풀 설정을 정의합니다.
 *
 * @since 1.0
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  /**
   * 비동기 작업을 위한 Executor를 설정합니다.
   *
   * <p>ThreadPoolTaskExecutor를 사용하여 스레드 풀을 생성합니다:
   * <ul>
   *   <li>코어 스레드 수: 10</li>
   *   <li>최대 스레드 수: 10</li>
   *   <li>큐 용량: 200</li>
   *   <li>스레드 이름 접두사: async-holiday-</li>
   * </ul>
   *
   * @return 비동기 작업 실행기
   * @since 1.0
   */
  @Override
  public Executor getAsyncExecutor() {
    return holidayTaskExecutor();
  }

  /**
   * 공휴일 동기화를 위한 전용 Executor Bean.
   *
   * <p>ThreadPoolTaskExecutor를 사용하여 스레드 풀을 생성합니다:
   * <ul>
   *   <li>코어 스레드 수: 10</li>
   *   <li>최대 스레드 수: 10</li>
   *   <li>큐 용량: 200</li>
   *   <li>스레드 이름 접두사: async-holiday-</li>
   *   <li>Graceful Shutdown: 활성화 (10초 대기)</li>
   * </ul>
   *
   * <p>이 Executor는 {@code @Async} 어노테이션의 기본 실행기로 사용되며,
   * {@code HolidayService}에서 명시적으로 주입받아 CompletableFuture와 함께 사용됩니다.
   *
   * @return 공휴일 작업 실행기
   * @since 1.0
   */
  @Bean(name = "holidayTaskExecutor")
  public Executor holidayTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(2000);
    executor.setThreadNamePrefix("async-holiday-");
    executor.setTaskDecorator(new JobIdTaskDecorator());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.initialize();
    return executor;
  }
}
