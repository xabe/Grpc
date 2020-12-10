package com.xabe.spring.grpc.infrastructure.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.protobuf.ByteString;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarData;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarInfo;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageResponseOuterClass.UploadCarImageResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.infrastructure.application.CarUseCase;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

class UploadCarImageStreamObserverTest {

  private StreamObserver streamObserver;

  private CarUseCase carUseCase;

  private UploadCarImageStreamObserver uploadCarImageStreamObserver;

  @BeforeEach
  public void setUp() throws Exception {
    this.streamObserver = mock(StreamObserver.class);
    this.carUseCase = mock(CarUseCase.class);
    this.uploadCarImageStreamObserver = new UploadCarImageStreamObserver(this.streamObserver, this.carUseCase);
  }

  @Test
  public void givenAUploadCarImageRequestCarInfoWhenInvokeOnNextThenReturnCarDO() throws Exception {
    final UploadCarImageRequest uploadCarImageRequest =
        UploadCarImageRequest.newBuilder().setCarInfo(CarInfo.newBuilder().setId("id").build()).build();

    when(this.carUseCase.getCar(eq("id"))).thenReturn(CarDO.builder().build());

    this.uploadCarImageStreamObserver.onNext(uploadCarImageRequest);

    verify(this.carUseCase).getCar(eq("id"));
  }

  @Test
  public void givenAUploadCarImageRequestCarDataWhenInvokeOnNextThenReturnCarDO() throws Exception {
    final UploadCarImageRequest uploadCarImageInfoRequest =
        UploadCarImageRequest.newBuilder().setCarInfo(CarInfo.newBuilder().setId("id").build()).build();
    final ArgumentCaptor<UploadCarImageResponse> responseArgumentCaptor = ArgumentCaptor.forClass(UploadCarImageResponse.class);

    when(this.carUseCase.getCar(eq("id"))).thenReturn(CarDO.builder().id("id").brand("brand").model("model").build());

    this.uploadCarImageStreamObserver.onNext(uploadCarImageInfoRequest);

    final UploadCarImageRequest uploadCarImageDataRequest =
        UploadCarImageRequest.newBuilder().setCarData(CarData.newBuilder().setChunkData(ByteString.EMPTY).build()).build();

    this.uploadCarImageStreamObserver.onNext(uploadCarImageDataRequest);
    this.uploadCarImageStreamObserver.onCompleted();

    verify(this.carUseCase).getCar(eq("id"));
    final InOrder order = inOrder(this.streamObserver);
    verify(this.streamObserver).onNext(responseArgumentCaptor.capture());
    order.verify(this.streamObserver).onCompleted();
    final UploadCarImageResponse result = responseArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getModel(), is("model"));
    assertThat(result.getBrand(), is("brand"));
    assertThat(result.getSize().getValue(), is(0));
  }

}