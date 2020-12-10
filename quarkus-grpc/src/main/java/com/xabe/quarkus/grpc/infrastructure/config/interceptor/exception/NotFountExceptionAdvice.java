package com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception;

import com.xabe.quarkus.grpc.domain.exception.NotFoundException;
import com.xabe.quarkus.grpc.infrastructure.config.interceptor.GrpcException;
import io.grpc.Status;
import io.grpc.Status.Code;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotFountExceptionAdvice implements GrpcException<NotFoundException> {

  @Override
  public Class<NotFoundException> getType() {
    return NotFoundException.class;
  }

  @Override
  public GrpcFrameworkException handleException(final NotFoundException exception) {
    return new GrpcFrameworkException(
        new GrpcProblemDetails(Code.NOT_FOUND.value(), exception.getMessage(), exception.toString()),
        Status.NOT_FOUND);
  }
}
