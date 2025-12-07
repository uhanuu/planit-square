package com.planitsquare.miniservice.application.aspect;

import com.planitsquare.miniservice.application.annotation.RecordSyncHistory;
import com.planitsquare.miniservice.application.port.out.RecordSyncHistoryPort;
import com.planitsquare.miniservice.domain.vo.Country;
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

import java.time.LocalDateTime;

/**
 * {@link RecordSyncHistory} 어노테이션을 처리하는 AOP Aspect.
 *
 * <p>메서드 실행의 성공/실패를 자동으로 기록합니다:
 * <ul>
 *   <li>성공 시: {@link RecordSyncHistoryPort#recordSuccess} 호출</li>
 *   <li>실패 시: {@link RecordSyncHistoryPort#recordFailure} 호출</li>
 * </ul>
 *
 * <p>실행 시간을 측정하여 {@code durationMillis}로 기록합니다.
 *
 * @since 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncHistoryAspect {

  private final RecordSyncHistoryPort recordSyncHistoryPort;
  private final ExpressionParser parser = new SpelExpressionParser();

  /**
   * {@link RecordSyncHistory} 어노테이션이 적용된 메서드를 가로채서 이력을 기록합니다.
   *
   * <p>Job ID, 국가, 연도는 SpEL 표현식을 통해 메서드 파라미터에서 추출됩니다.
   *
   * @param joinPoint AOP Join Point
   * @param recordSyncHistory {@link RecordSyncHistory} 어노테이션
   * @return 메서드 실행 결과
   * @throws Throwable 메서드 실행 중 발생한 예외
   * @since 1.0
   */
  @Around("@annotation(recordSyncHistory)")
  public Object handleRecordSyncHistory(
      ProceedingJoinPoint joinPoint,
      RecordSyncHistory recordSyncHistory
  ) throws Throwable {
    Long jobId = extractValue(joinPoint, recordSyncHistory.jobId(), Long.class);
    Country country = extractValue(joinPoint, recordSyncHistory.country(), Country.class);
    Integer year = extractValue(joinPoint, recordSyncHistory.year(), Integer.class);

    long startTime = System.currentTimeMillis();
    LocalDateTime syncedAt = LocalDateTime.now();

    try {
      Object result = joinPoint.proceed();

      long durationMillis = System.currentTimeMillis() - startTime;

      int syncedCount = 0;
      if (result instanceof java.util.List) {
        syncedCount = ((java.util.List<?>) result).size();
      }

      recordSyncHistoryPort.recordSuccess(
          jobId,
          country,
          year,
          syncedCount,
          durationMillis,
          syncedAt
      );

      log.debug("동기화 성공 기록 - Job ID: {}, Country: {}, Year: {}, Count: {}",
          jobId, country.getCode(), year, syncedCount);

      return result;

    } catch (Exception e) {
      // 실패 기록
      long durationMillis = System.currentTimeMillis() - startTime;

      recordSyncHistoryPort.recordFailure(
          jobId,
          country,
          year,
          e.getMessage(),
          durationMillis,
          syncedAt
      );

      log.error("동기화 실패 기록 - Job ID: {}, Country: {}, Year: {}, Error: {}",
          jobId, country.getCode(), year, e.getMessage());

      // 예외를 다시 던져서 트랜잭션 롤백 유도
      throw e;
    }
  }

  /**
   * SpEL 표현식을 사용하여 값을 추출합니다.
   *
   * @param joinPoint AOP Join Point
   * @param expression SpEL 표현식
   * @param targetType 대상 타입
   * @param <T> 반환 타입
   * @return 추출된 값
   * @since 1.0
   */
  private <T> T extractValue(ProceedingJoinPoint joinPoint, String expression, Class<T> targetType) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    EvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    return parser.parseExpression(expression).getValue(context, targetType);
  }
}
