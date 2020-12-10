package com.xabe.spring.grpc.infrastructure.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.google.protobuf.ByteString;
import com.xabe.car.api.grpc.getcar.GetCarRequestOuterClass.GetCarRequest;
import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.searchcar.SearchCarRequestOuterClass.SearchCarRequest;
import com.xabe.car.api.grpc.searchcar.SearchCarResponseOuterClass.SearchCarResponse;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarData;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarInfo;
import com.xabe.spring.grpc.App;
import com.xabe.spring.grpc.domain.entity.CarDO;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CarGrpcControllerIT {

  @Test
  public void givenACarIDWhenInvokeGetCarThenReturnCar() throws Exception {
    final GetCarRequest getCarRequest = GetCarRequest.newBuilder().setId("1").build();

    final GetCarResponse result = GrpcUtil.instance().getCarEndPointBlockingStub().getCar(getCarRequest);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("1"));
    assertThat(result.getModel(), is("Mazda3"));
    assertThat(result.getBrand(), is("Mazda"));
  }

  @Test
  public void givenACarIDWhenInvokeGetCarThenReturnException() throws Exception {
    final GetCarRequest getCarRequest = GetCarRequest.newBuilder().setId("idNot").build();

    try {
      GrpcUtil.instance().getCarEndPointBlockingStub().getCar(getCarRequest);
    } catch (final StatusRuntimeException e) {
      assertThat(e, is(notNullValue()));
      assertThat(e.getStatus().getCode(), is(Status.NOT_FOUND.getCode()));
      assertThat(e.getStatus().getDescription(), is("Not found car with id : idNot"));
    }
  }

  @Test
  public void givenAFilterStreamWhenInvokeGetCarThenReturnAllCar() throws Exception {
    final SearchCarRequest searchCarRequest = SearchCarRequest.newBuilder().setBrand("mazda").build();
    final SearchCarResponseStreamObserver searchCarResponseStreamObserver = new SearchCarResponseStreamObserver();

    GrpcUtil.instance().getCarEndPointStub().searchCar(searchCarRequest, searchCarResponseStreamObserver);
    final List<CarDO> result = searchCarResponseStreamObserver.getCompletableFuture().get(20, TimeUnit.SECONDS);

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(greaterThan(1)));
  }

  @Test
  public void givenAFilterBlockWhenInvokeGetCarThenReturnAllCar() throws Exception {
    final SearchCarRequest searchCarRequest = SearchCarRequest.newBuilder().setBrand("mazda").build();

    final Iterator<SearchCarResponse> iterator = GrpcUtil.instance().getCarEndPointBlockingStub().searchCar(searchCarRequest);
    final List<CarDO> result = new LinkedList<>();
    iterator.forEachRemaining(item -> {
      result.add(CarDO.builder().id(item.getId()).model(item.getModel()).brand(item.getBrand()).build());
    });

    assertThat(result, is(notNullValue()));
    assertThat(result.size(), is(greaterThan(1)));
  }

  @Test
  public void givenAFileWhenInvokeUploadCarImage() throws Exception {
    final String path = getClass().getClassLoader().getResource("mazda").getPath();
    final String decodePath = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
    final UploadCarResponseStreamObserver uploadCarResponseStreamObserver = new UploadCarResponseStreamObserver();

    try (final FileInputStream fileInputStream = new FileInputStream(decodePath + File.separator + "Mazda3.jpg")) {

      final StreamObserver<UploadCarImageRequest> uploadCarImageRequestStreamObserver =
          GrpcUtil.instance().getCarEndPointStub().uploadCarImage(uploadCarResponseStreamObserver);
      final UploadCarImageRequest carImageRequest =
          UploadCarImageRequest.newBuilder().setCarInfo(CarInfo.newBuilder().setId("1").build()).build();

      uploadCarImageRequestStreamObserver.onNext(carImageRequest);

      final byte[] buffer = new byte[1024];
      int size = 0;
      while (true) {
        final int n = fileInputStream.read(buffer);
        if (n <= 0) {
          break;
        }
        size += n;
        uploadCarImageRequestStreamObserver.onNext(UploadCarImageRequest.newBuilder().setCarData(
            CarData.newBuilder().setChunkData(ByteString.copyFrom(buffer, 0, n)).build()).build());
      }
      uploadCarImageRequestStreamObserver.onCompleted();
      final CompletableFuture<Integer> completableFuture = uploadCarResponseStreamObserver.getCompletableFuture();

      final Integer result = completableFuture.get(10, TimeUnit.SECONDS);

      assertThat(result, is(size));
    }
  }
}
