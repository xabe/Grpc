package com.xabe.quarkus.grpc.domain.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException(final RuntimeException e) {
    super(e);
  }

  public NotFoundException(final String message) {
    super(message);
  }
}
