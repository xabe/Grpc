package com.xabe.spring.grpc.infrastructure.config.grpc.aspect;

import com.google.protobuf.Any;
import com.google.rpc.Status;
import com.xabe.grpc.exception.ProblemDetailsOuterClass.ProblemDetails;
import com.xabe.grpc.exception.ProblemDetailsOuterClass.ProblemDetails.Builder;
import com.xabe.spring.grpc.infrastructure.config.grpc.exception.GrpcFrameworkException;
import com.xabe.spring.grpc.infrastructure.config.grpc.exception.GrpcProblemDetails;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

@Component
public class AdvisorGrpcExceptionConverter implements GrpcExceptionConverter, InitializingBean {

  private final GrpcExceptionAdvice defaultGrpcControllerAdvice = e -> (GrpcFrameworkException) e;

  private final List<GrpcExceptionAdvice> advices;

  private final Map<Class<? extends Exception>, GrpcExceptionAdvice> adviceCache;

  @Autowired
  public AdvisorGrpcExceptionConverter(final List<GrpcExceptionAdvice> advices) {
    this.advices = advices;
    this.adviceCache = new ConcurrentHashMap();
  }

  @Override
  public StatusRuntimeException convert(final RuntimeException ex) {
    final GrpcFrameworkException e = this.getAdvice(ex).handleException(ex);
    final Status errorStatus = toRpcStatus(e);
    final Metadata metadata = new Metadata();
    return StatusProto.toStatusRuntimeException(errorStatus, metadata);
  }

  private <T extends RuntimeException> GrpcExceptionAdvice<T> getAdvice(final T ex) {
    GrpcExceptionAdvice controller = null;
    Class exceptionClass = ex.getClass();

    do {
      controller = (GrpcExceptionAdvice) this.adviceCache.get(exceptionClass);
      if (controller != null) {
        if (!this.adviceCache.containsKey(ex.getClass())) {
          this.adviceCache.put(ex.getClass(), controller);
        }

        return controller;
      }

      exceptionClass = exceptionClass.getSuperclass();
    } while (!exceptionClass.equals(Exception.class));

    this.adviceCache.put(ex.getClass(), this.defaultGrpcControllerAdvice);
    return this.defaultGrpcControllerAdvice;
  }

  public static com.google.rpc.Status toRpcStatus(final GrpcFrameworkException grpcFrameworkException) {
    final GrpcProblemDetails details = grpcFrameworkException.getDetails();
    final Builder problemDetails =
        ProblemDetails.newBuilder().setCode(details.getCode()).setTitle(details.getTitle()).setDetail(details.getDetail());
    final Any anyDetails = Any.pack(problemDetails.build());
    return com.google.rpc.Status.newBuilder().setCode(grpcFrameworkException.getStatusCode().getCode().value())
        .setMessage(details.getTitle())
        .addDetails(anyDetails).build();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.advices.forEach((e) -> {
      final Class generic = GenericTypeResolver.resolveTypeArgument(e.getClass(), GrpcExceptionAdvice.class);
      if (generic != null) {
        this.adviceCache.put(generic, e);
      }
    });
  }
}
