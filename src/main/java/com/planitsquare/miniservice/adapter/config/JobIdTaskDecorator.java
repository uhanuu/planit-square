package com.planitsquare.miniservice.adapter.config;

import com.planitsquare.miniservice.application.util.JobIdContext;
import org.springframework.core.task.TaskDecorator;

/**
 * ThreadLocal 컨텍스트를 비동기 스레드로 전파하는 TaskDecorator.
 *
 * <p>부모 스레드의 Job ID를 자식 비동기 스레드로 전달합니다.
 * 비동기 작업 실행 시 ThreadLocal에 저장된 컨텍스트가 유실되는 것을 방지합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * @Bean
 * public Executor taskExecutor() {
 *   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *   executor.setTaskDecorator(new JobIdTaskDecorator());
 *   executor.initialize();
 *   return executor;
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class JobIdTaskDecorator implements TaskDecorator {

  /**
   * Runnable을 래핑하여 Job ID 컨텍스트를 전파합니다.
   *
   * <p>부모 스레드의 Job ID를 캡처하고, 비동기 작업 실행 전에 설정합니다.
   * 작업 완료 후에는 반드시 컨텍스트를 정리하여 메모리 누수를 방지합니다.
   *
   * @param runnable 원본 작업
   * @return Job ID가 전파된 작업
   * @since 1.0
   */
  @Override
  public Runnable decorate(Runnable runnable) {
    Long jobId = JobIdContext.getJobId();
    return () -> {
      try {
        if (jobId != null) {
          JobIdContext.setJobId(jobId);
        }
        runnable.run();
      } finally {
        JobIdContext.clear();
      }
    };
  }
}
