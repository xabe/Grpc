package com.xabe.quarkus.grpc.infrastructure.config.interceptor;

import com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception.GrpcFrameworkException;
import com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception.GrpcProblemDetails;
import io.grpc.Status;
import io.grpc.Status.Code;

public interface GrpcException<T extends RuntimeException> {

  GrpcException<RuntimeException> DEFAULT = new GrpcException() {
    @Override
    public Class getType() {
      return null;
    }

    @Override
    public GrpcFrameworkException handleException(final RuntimeException exception) {
      return new GrpcFrameworkException(new GrpcProblemDetails(Code.UNKNOWN.value(), exception.getMessage(), exception.toString()),
          Status.UNKNOWN);
    }
  };

  Class<T> getType();

  GrpcFrameworkException handleException(T exception);

}
