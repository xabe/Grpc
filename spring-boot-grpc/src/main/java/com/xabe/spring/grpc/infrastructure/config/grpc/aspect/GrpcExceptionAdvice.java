package com.xabe.spring.grpc.infrastructure.config.grpc.aspect;

import com.xabe.spring.grpc.infrastructure.config.grpc.exception.GrpcFrameworkException;

@FunctionalInterface
public interface GrpcExceptionAdvice<T extends RuntimeException> {

  GrpcFrameworkException handleException(T exception);

}
