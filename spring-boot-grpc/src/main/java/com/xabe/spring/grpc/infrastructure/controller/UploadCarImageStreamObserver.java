package com.xabe.spring.grpc.infrastructure.controller;

import com.google.protobuf.ByteString;
import com.google.protobuf.Int32Value;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarData;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.CarInfo;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest.DataCase;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageResponseOuterClass.UploadCarImageResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.infrastructure.application.CarUseCase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadCarImageStreamObserver implements StreamObserver<UploadCarImageRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UploadCarImageStreamObserver.class);

  private final StreamObserver<UploadCarImageResponse> responseObserver;

  private final CarUseCase carUseCase;

  private CarDO carDO;

  private ByteArrayOutputStream imageData;

  private final Map<DataCase, Consumer<UploadCarImageRequest>> dataCaseConsumerMap = Map.of(
      DataCase.CAR_INFO, this::carInfoHandler,
      DataCase.CAR_DATA, this::carImageHandler
  );

  public UploadCarImageStreamObserver(final StreamObserver<UploadCarImageResponse> responseObserver, final CarUseCase carUseCase) {
    this.responseObserver = responseObserver;
    this.carUseCase = carUseCase;
  }

  @Override
  public void onNext(final UploadCarImageRequest uploadCarImageRequest) {
    final DataCase dataCase = uploadCarImageRequest.getDataCase();
    this.dataCaseConsumerMap.get(dataCase).accept(uploadCarImageRequest);
    LOGGER.info("Case : {}", dataCase);
  }

  private void carInfoHandler(final UploadCarImageRequest uploadCarImageRequest) {
    final CarInfo carInfo = uploadCarImageRequest.getCarInfo();
    this.carDO = this.carUseCase.getCar(carInfo.getId());
    this.imageData = new ByteArrayOutputStream();
    LOGGER.info("Found car {}", this.carDO);
  }

  private void carImageHandler(final UploadCarImageRequest uploadCarImageRequest) {
    final CarData carData = uploadCarImageRequest.getCarData();
    if (Objects.isNull(this.carDO)) {
      LOGGER.warn("Case data not found car");
      return;
    }
    final ByteString chunkData = carData.getChunkData();
    try {
      chunkData.writeTo(this.imageData);
      LOGGER.info("Upload data image car {} and size {}", this.carDO, this.imageData.size());
    } catch (final IOException e) {
      LOGGER.error("Upload data image car error {}", e.getMessage(), e);
      this.responseObserver.onError(
          Status.INTERNAL
              .withDescription("cannot write chunk data: " + e.getMessage())
              .asRuntimeException()
      );
    }
  }

  @Override
  public void onError(final Throwable throwable) {

  }

  @Override
  public void onCompleted() {
    if (Objects.isNull(this.carDO)) {
      this.responseObserver.onError(Status.NOT_FOUND.withDescription("Cannot write chunk data without car").asRuntimeException());
    } else {
      this.responseObserver
          .onNext(UploadCarImageResponse.newBuilder().setSize(Int32Value.newBuilder().setValue(this.imageData.size()).build())
              .setId(this.carDO.getId()).setBrand(this.carDO.getBrand())
              .setModel(this.carDO.getModel()).build());
    }
    this.responseObserver.onCompleted();
    LOGGER.info("Upload data image car completed");
  }
}
