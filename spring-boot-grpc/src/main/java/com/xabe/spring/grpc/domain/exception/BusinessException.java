package com.xabe.spring.grpc.domain.exception;

public class BusinessException extends RuntimeException {

  public BusinessException(final RuntimeException e) {
    super(e);
  }

  public BusinessException(final String message) {
    super(message);
  }
}
