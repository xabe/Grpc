package com.xabe.spring.grpc.infrastructure.config.grpc.interceptor;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
public class ExceptionInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall, final Metadata metadata,
      final ServerCallHandler<ReqT, RespT> serverCallHandler) {
    final ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
    return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata);
  }

  private class ExceptionHandlingServerCallListener<ReqT, RespT>
      extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

    private final ServerCall<ReqT, RespT> serverCall;

    private final Metadata metadata;

    ExceptionHandlingServerCallListener(final ServerCall.Listener<ReqT> listener, final ServerCall<ReqT, RespT> serverCall,
        final Metadata metadata) {
      super(listener);
      this.serverCall = serverCall;
      this.metadata = metadata;
    }

    @Override
    public void onHalfClose() {
      try {
        super.onHalfClose();
      } catch (final RuntimeException ex) {
        handleException(ex, this.serverCall, this.metadata);
        throw ex;
      }
    }

    @Override
    public void onReady() {
      try {
        super.onReady();
      } catch (final RuntimeException ex) {
        handleException(ex, this.serverCall, this.metadata);
        throw ex;
      }
    }

    private void handleException(final RuntimeException exception, final ServerCall<ReqT, RespT> serverCall, final Metadata metadata) {
      if (exception instanceof IllegalArgumentException) {
        serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
      } else if (StatusRuntimeException.class.isInstance(exception)) {
        final StatusRuntimeException statusRuntimeException = StatusRuntimeException.class.cast(exception);
        serverCall.close(statusRuntimeException.getStatus(), metadata);
      } else {
        serverCall.close(Status.UNKNOWN, metadata);
      }
    }
  }
}