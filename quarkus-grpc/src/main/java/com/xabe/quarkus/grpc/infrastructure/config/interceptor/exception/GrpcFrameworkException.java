package com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception;

import io.grpc.Status;

public class GrpcFrameworkException extends RuntimeException {

  private final GrpcProblemDetails details;

  private final Status statusCode;

  public GrpcFrameworkException(final GrpcProblemDetails details, final Status statusCode) {
    super(details.getTitle());
    this.details = details;
    this.statusCode = statusCode;
  }

  public GrpcProblemDetails getDetails() {
    return this.details;
  }

  public Status getStatusCode() {
    return this.statusCode;
  }

}
