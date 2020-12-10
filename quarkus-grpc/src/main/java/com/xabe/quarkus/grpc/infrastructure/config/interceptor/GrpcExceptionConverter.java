package com.xabe.quarkus.grpc.infrastructure.config.interceptor;

import com.google.protobuf.Any;
import com.google.rpc.Status;
import com.xabe.grpc.exception.ProblemDetailsOuterClass.ProblemDetails;
import com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception.GrpcFrameworkException;
import com.xabe.quarkus.grpc.infrastructure.config.interceptor.exception.GrpcProblemDetails;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.quarkus.runtime.Startup;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
@Startup
public class GrpcExceptionConverter {

  @Inject
  Instance<GrpcException<? extends RuntimeException>> advices;

  private final Map<Class<? extends Exception>, GrpcException> adviceCache = new ConcurrentHashMap();

  @PostConstruct
  public void init() {
    this.advices.stream().forEach(item -> this.adviceCache.put(item.getType(), item));
  }

  public StatusRuntimeException convert(final RuntimeException ex) {
    final GrpcFrameworkException e = this.getAdvice(ex).handleException(ex);
    final Status errorStatus = toRpcStatus(e);
    final Metadata metadata = new Metadata();
    return StatusProto.toStatusRuntimeException(errorStatus, metadata);
  }

  private <T extends RuntimeException> GrpcException<T> getAdvice(final T ex) {
    GrpcException controller = null;
    Class exceptionClass = ex.getClass();

    do {
      controller = (GrpcException) this.adviceCache.get(exceptionClass);
      if (controller != null) {
        if (!this.adviceCache.containsKey(ex.getClass())) {
          this.adviceCache.put(ex.getClass(), controller);
        }

        return controller;
      }

      exceptionClass = exceptionClass.getSuperclass();
    } while (!exceptionClass.equals(Exception.class));

    this.adviceCache.put(ex.getClass(), GrpcException.DEFAULT);
    return (GrpcException<T>) GrpcException.DEFAULT;
  }

  public static Status toRpcStatus(final GrpcFrameworkException e) {
    final GrpcProblemDetails details = e.getDetails();
    final ProblemDetails.Builder problemDetails =
        ProblemDetails.newBuilder().setCode(details.getCode()).setTitle(details.getTitle()).setDetail(details.getDetail());
    final Any anyDetails = Any.pack(problemDetails.build());
    return Status.newBuilder().setCode(e.getStatusCode().getCode().value()).setMessage(details.getTitle())
        .addDetails(anyDetails).build();
  }

}
