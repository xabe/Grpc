package com.xabe.quarkus.grpc.infrastructure.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.car.api.grpc.getcar.GetCarRequestOuterClass.GetCarRequest;
import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarRequestOuterClass.GetNavigateCarRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.quarkus.test.junit.QuarkusTest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
class CarGrpcControllerTest {

  @Test
  public void givenACarIDWhenInvokeGetCarThenReturnCar() throws Exception {
    final GetCarRequest getCarRequest = GetCarRequest.newBuilder().setId("1").build();

    final GetCarResponse result = GrpcUtil.instance().getCarEndPointBlockingStub().getCar(getCarRequest);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("1"));
    assertThat(result.getName(), is("Mazda"));
  }

  @Test
  public void givenACarIDWhenInvokeGetCarThenReturnException() throws Exception {
    final GetCarRequest getCarRequest = GetCarRequest.newBuilder().setId("not").build();

    try {
      GrpcUtil.instance().getCarEndPointBlockingStub().getCar(getCarRequest);
    } catch (final StatusRuntimeException e) {
      assertThat(e, is(notNullValue()));
      assertThat(e.getStatus().getCode(), is(Status.NOT_FOUND.getCode()));
      assertThat(e.getStatus().getDescription(), is("Not found car with id : not"));
    }
  }

  @Test
  public void givenACarIdWhenInvokeGetNavigateCarThenReturnStream() throws Exception {
    final GetNavigateResponseStreamObserver streamObserver = new GetNavigateResponseStreamObserver("1");
    final StreamObserver<GetNavigateCarRequest> request = GrpcUtil.instance().getCarEndPointStub().getNavigateCar(streamObserver);
    final CompletableFuture<Object> completableFuture = streamObserver.startTrip(request);

    final Object result = completableFuture.get(30, TimeUnit.SECONDS);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void givenACarIdWhenInvokeGetNavigateCarThenReturnErrorStream() throws Exception {
    final GetNavigateResponseStreamObserver streamObserver = new GetNavigateResponseStreamObserver("not");

    try {
      final StreamObserver<GetNavigateCarRequest> request = GrpcUtil.instance().getCarEndPointStub().getNavigateCar(streamObserver);
      streamObserver.startTrip(request);
    } catch (final StatusRuntimeException e) {
      assertThat(e, is(notNullValue()));
      assertThat(e.getStatus().getCode(), is(Status.NOT_FOUND.getCode()));
      assertThat(e.getStatus().getDescription(), is("Not found car with id : not"));
    }
  }

}