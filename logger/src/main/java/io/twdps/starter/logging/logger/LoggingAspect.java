package io.twdps.starter.logging.logger;

import io.twdps.starter.logging.logger.annotations.LoggableEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 50)
public class LoggingAspect {

  @Around("@within(io.twdps.starter.logging.logger.annotations.Loggable) || @annotation(io.twdps.starter.logging.logger.annotations.Loggable)")
  public Object logMethodEntryExit(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    String className = pjp.getSignature().getDeclaringTypeName();
    String methodName = pjp.getSignature().getName();
    String argumentsToString = getMethodArguments(pjp);

    log.debug(String.format("Entering method %s.%s(%s)", className, methodName, argumentsToString));

    Object result = pjp.proceed();

    long elapsedTime = System.currentTimeMillis() - start;
    log.debug(String.format("Exiting method %s.%s; execution time (ms): %s; response: %s;", className, methodName, elapsedTime, result));
    return result;
  }

  @Around("@annotation(loggableEvent)")
  public Object loggableEvent(ProceedingJoinPoint jp, LoggableEvent loggableEvent) throws Throwable {
    long start = System.currentTimeMillis();
    try {
      Object result = jp.proceed();

      if (result instanceof ResponseEntity && isErrorCode((ResponseEntity) result)) {
        logEvent(loggableEvent, start, LogEvent.FAILED, String.format("Status Code: %s, Error: %s", ((ResponseEntity) result).getStatusCode(), ((ResponseEntity) result).getBody()));
        return result;
      }

      logEvent(loggableEvent, start, LogEvent.SUCCESS, successfulMessage(jp));
      return result;

    } catch (Throwable throwable) {
      logEvent(loggableEvent, start, LogEvent.FAILED, throwable.getMessage());
      throw throwable;
    }
  }

  private String successfulMessage(ProceedingJoinPoint jp) {
    return String.format("Successful call: %s.%s ", jp.getSignature().getDeclaringTypeName(), jp.getSignature().getName());
  }

  private void logEvent(LoggableEvent loggableEvent, long start, String status, String object) {
    long l = System.currentTimeMillis() - start;
    MDC.put("status", status);
    MDC.put("duration_millis", String.valueOf(l));
    MDC.put("action", loggableEvent.action());
    MDC.put("application_tier", loggableEvent.applicationTier().toString());

    logEvent(object);
  }

  private boolean isErrorCode(ResponseEntity result) {
    return result.getStatusCode().is4xxClientError() || result.getStatusCode().is5xxServerError();
  }

  private void logEvent(Object response) {
    LogEvent.builder()
        .message(response.toString())
        .build()
        .log();

  }

  private String getMethodArguments(ProceedingJoinPoint jp) {
    return Arrays.stream(jp.getArgs())
        .map(arg -> (arg == null) ? null : arg.toString())
        .collect(Collectors.joining(","));
  }
}
