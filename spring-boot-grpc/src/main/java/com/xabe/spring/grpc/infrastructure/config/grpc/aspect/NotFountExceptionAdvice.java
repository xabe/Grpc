package com.xabe.spring.grpc.infrastructure.config.grpc.aspect;

import com.xabe.spring.grpc.domain.exception.NotFoundException;
import com.xabe.spring.grpc.infrastructure.config.grpc.exception.GrpcFrameworkException;
import com.xabe.spring.grpc.infrastructure.config.grpc.exception.GrpcProblemDetails;
import io.grpc.Status;
import io.grpc.Status.Code;
import org.springframework.stereotype.Component;

@Component
public class NotFountExceptionAdvice implements GrpcExceptionAdvice<NotFoundException> {

  @Override
  public GrpcFrameworkException handleException(final NotFoundException exception) {
    return new GrpcFrameworkException(
        new GrpcProblemDetails(Code.NOT_FOUND.value(), exception.getMessage(), exception.toString()),
        Status.NOT_FOUND);
  }

}
