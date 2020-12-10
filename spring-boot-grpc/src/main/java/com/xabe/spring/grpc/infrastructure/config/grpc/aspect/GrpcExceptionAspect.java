package com.xabe.spring.grpc.infrastructure.config.grpc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GrpcExceptionAspect {

  private final GrpcExceptionConverter grpcExceptionConverter;

  public GrpcExceptionAspect(final GrpcExceptionConverter grpcExceptionConverter) {
    this.grpcExceptionConverter = grpcExceptionConverter;
  }

  @Pointcut("within(@net.devh.boot.grpc.server.service.GrpcService *)")
  public void grpcService() {
  }

  @Pointcut("execution(public * *(..))")
  public void publicMethod() {
  }

  @Pointcut("grpcService() && publicMethod()")
  public void publicMethodGrpcService() {
  }

  @Around("publicMethodGrpcService()")
  public Object annotationAround(final ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      return joinPoint.proceed();
    } catch (final RuntimeException var5) {
      throw this.convert(var5);
    }
  }

  private RuntimeException convert(final RuntimeException e) {
    RuntimeException result = e;
    if (this.grpcExceptionConverter != null) {
      result = this.grpcExceptionConverter.convert(e);
    }
    return (RuntimeException) result;
  }
}
