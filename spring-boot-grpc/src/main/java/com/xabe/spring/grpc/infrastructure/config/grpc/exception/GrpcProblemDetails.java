package com.xabe.spring.grpc.infrastructure.config.grpc.exception;

import java.io.Serializable;

public class GrpcProblemDetails implements Serializable {

  private final int code;

  private final String title;

  private final String detail;

  public GrpcProblemDetails(final int code, final String title, final String detail) {

    this.code = code;
    this.title = title;
    this.detail = detail;
  }

  public int getCode() {
    return this.code;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDetail() {
    return this.detail;
  }
}
