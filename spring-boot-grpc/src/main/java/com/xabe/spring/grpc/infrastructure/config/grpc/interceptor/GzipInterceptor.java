package com.xabe.spring.grpc.infrastructure.config.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class GzipInterceptor implements ServerInterceptor {

  public static final String GZIP = "gzip";

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers,
      final ServerCallHandler<ReqT, RespT> next) {
    call.setCompression(GZIP);
    return next.startCall(call, headers);
  }
}
