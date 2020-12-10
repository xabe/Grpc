package com.xabe.spring.grpc.infrastructure.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.car.api.grpc.getcar.GetCarRequestOuterClass.GetCarRequest;
import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.searchcar.SearchCarRequestOuterClass.SearchCarRequest;
import com.xabe.car.api.grpc.searchcar.SearchCarResponseOuterClass.SearchCarResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.infrastructure.application.CarUseCase;
import com.xabe.spring.grpc.infrastructure.controller.mapper.CarGrpcMapper;
import io.grpc.stub.StreamObserver;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class CarGrpcControllerTest {

  private CarUseCase carUseCase;

  private CarGrpcMapper carGrpcMapper;

  private CarGrpcController carGrpcController;

  @BeforeEach
  public void setUp() throws Exception {
    this.carUseCase = mock(CarUseCase.class);
    this.carGrpcMapper = mock(CarGrpcMapper.class);
    this.carGrpcController = new CarGrpcController(this.carUseCase, this.carGrpcMapper);
  }

  @Test
  public void givenAGetCarRequestWhenInvokeGetCarThenReturnGetCarResponse() throws Exception {
    final GetCarRequest getCarRequest = GetCarRequest.newBuilder().setId("id").build();
    final StreamObserver streamObserver = mock(StreamObserver.class);

    final CarDO carDO = CarDO.builder().build();
    when(this.carUseCase.getCar(eq("id"))).thenReturn(carDO);
    final GetCarResponse carResponse = GetCarResponse.newBuilder().build();
    when(this.carGrpcMapper.mapCarGrpc(eq(carDO))).thenReturn(carResponse);

    this.carGrpcController.getCar(getCarRequest, streamObserver);

    final InOrder order = inOrder(streamObserver);
    order.verify(streamObserver).onNext(eq(carResponse));
    order.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenASearchCarRequestWhenInvokeSearchCarThenReturnStream() throws Exception {
    final SearchCarRequest searchCarRequest = SearchCarRequest.newBuilder().setBrand("brand").build();
    final StreamObserver streamObserver = mock(StreamObserver.class);
    final CarDO carDO = CarDO.builder().build();
    final SearchCarResponse defaultInstance = SearchCarResponse.getDefaultInstance();

    doAnswer(invocationOnMock -> {
      final Consumer consumer = invocationOnMock.getArgument(2, Consumer.class);
      consumer.accept(carDO);
      return null;
    }).when(this.carUseCase).search(any(), any(), any());
    when(this.carGrpcMapper.mapSearchCarGrpc(eq(carDO))).thenReturn(defaultInstance);

    this.carGrpcController.searchCar(searchCarRequest, streamObserver);

    final InOrder order = inOrder(streamObserver);
    order.verify(streamObserver).onNext(eq(defaultInstance));
    order.verify(streamObserver).onCompleted();
  }

  @Test
  public void givenAUploadCarImageResponseWhenInvokeUploadCarImageThenReturnUploadCarImageRequest() throws Exception {
    final StreamObserver streamObserver = mock(StreamObserver.class);

    final StreamObserver result = this.carGrpcController.uploadCarImage(streamObserver);

    assertThat(result, is(notNullValue()));
  }

}