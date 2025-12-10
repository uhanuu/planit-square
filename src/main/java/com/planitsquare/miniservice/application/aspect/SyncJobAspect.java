package com.planitsquare.miniservice.application.aspect;

import com.planitsquare.miniservice.adapter.out.persistence.vo.SyncExecutionType;
import com.planitsquare.miniservice.application.annotation.SyncJob;
import com.planitsquare.miniservice.application.port.out.SyncJobPort;
import com.planitsquare.miniservice.application.service.SyncResult;
import com.planitsquare.miniservice.application.service.SyncStats;
import com.planitsquare.miniservice.application.util.JobIdContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@link SyncJob} 어노테이션을 처리하는 AOP Aspect.
 *
 * <p>Job의 생명주기를 자동으로 관리합니다:
 * <ul>
 *   <li>메서드 실행 전: Job 시작 ({@link SyncJobPort#startJob})</li>
 *   <li>메서드 실행 후: Job 완료 ({@link SyncJobPort#completeJob})</li>
 * </ul>
 *
 * <p>Job ID는 {@link JobIdContext}를 통해 관리되어 하위 메서드에서 접근할 수 있습니다.
 *
 * @since 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncJobAspect {

  private static final int SINGLE_TASK = 1;
  private static final int NO_SUCCESS = 0;
  private static final int SINGLE_FAILURE = 1;

  private final SyncJobPort syncJobPort;
  private final ExpressionParser parser = new SpelExpressionParser();

  /**
   * {@link SyncJob} 어노테이션이 적용된 메서드를 가로채서 Job 생명주기를 관리합니다.
   *
   * @param joinPoint AOP Join Point
   * @param syncJob {@link SyncJob} 어노테이션
   * @return 메서드 실행 결과
   * @throws Throwable 메서드 실행 중 발생한 예외
   * @since 1.0
   */
  @Around("@annotation(syncJob)")
  public Object handleSyncJob(ProceedingJoinPoint joinPoint, SyncJob syncJob) throws Throwable {
    // SpEL을 사용하여 executionType 추출
    SyncExecutionType executionType = extractExecutionType(joinPoint, syncJob.executionType());

    // Job 시작
    Long jobId = syncJobPort.startJob(executionType);
    JobIdContext.setJobId(jobId);
    log.info("Job 시작 - Job ID: {}, ExecutionType: {}", jobId, executionType);

    try {
      Object result = joinPoint.proceed();

      // 반환값이 List<SyncResult>인 경우 통계와 함께 Job 완료
      if (result instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof SyncResult) {
        @SuppressWarnings("unchecked")
        List<SyncResult> syncResults = (List<SyncResult>) result;
        SyncStats stats = SyncStats.from(syncResults);

        syncJobPort.completeJobWithStats(
            jobId,
            stats.totalTasks(),
            stats.successCount(),
            stats.failureCount()
        );

        log.info("Job 완료 (통계 포함) - Job ID: {}, {}", jobId, stats.toLogString());
      } else {
        // 기존 방식으로 완료
        syncJobPort.completeJob(jobId);
        log.info("Job 완료 - Job ID: {}", jobId);
      }

      return result;
    } catch (Exception e) {
      // 예외 발생 시 Job을 실패 상태로 처리
      syncJobPort.completeJobWithStats(jobId, SINGLE_TASK, NO_SUCCESS, SINGLE_FAILURE);
      log.error("Job 실패 - Job ID: {}, 예외: {}", jobId, e.getMessage(), e);
      throw e;  // 예외를 다시 던져서 상위에서 처리하도록 함
    } finally {
      // JobIdContext 정리
      JobIdContext.clear();
    }
  }

  /**
   * SpEL 표현식을 사용하여 executionType을 추출합니다.
   *
   * @param joinPoint AOP Join Point
   * @param expression SpEL 표현식
   * @return 추출된 SyncExecutionType
   * @since 1.0
   */
  private SyncExecutionType extractExecutionType(ProceedingJoinPoint joinPoint, String expression) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    EvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    return parser.parseExpression(expression).getValue(context, SyncExecutionType.class);
  }

}
