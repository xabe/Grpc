package com.xabe.spring.grpc.infrastructure.config.grpc.aspect;

import io.grpc.StatusRuntimeException;

public interface GrpcExceptionConverter {

  StatusRuntimeException convert(RuntimeException e);
}
