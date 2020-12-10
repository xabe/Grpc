package com.xabe.spring.grpc.infrastructure.controller;

import com.google.common.util.concurrent.Uninterruptibles;
import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointImplBase;
import com.xabe.car.api.grpc.getcar.GetCarRequestOuterClass.GetCarRequest;
import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.searchcar.SearchCarRequestOuterClass.SearchCarRequest;
import com.xabe.car.api.grpc.searchcar.SearchCarResponseOuterClass.SearchCarResponse;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageRequestOuterClass.UploadCarImageRequest;
import com.xabe.car.api.grpc.uploadcar.UploadCarImageResponseOuterClass.UploadCarImageResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.infrastructure.application.CarUseCase;
import com.xabe.spring.grpc.infrastructure.config.grpc.interceptor.GzipInterceptor;
import com.xabe.spring.grpc.infrastructure.controller.mapper.CarGrpcMapper;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService(interceptors = {GzipInterceptor.class})
public class CarGrpcController extends CarEndPointImplBase {

  private final CarUseCase carUseCase;

  private final CarGrpcMapper carGrpcMapper;

  @Autowired
  public CarGrpcController(final CarUseCase carUseCase, final CarGrpcMapper carGrpcMapper) {
    this.carUseCase = carUseCase;
    this.carGrpcMapper = carGrpcMapper;
  }

  @Override
  public void getCar(final GetCarRequest request, final StreamObserver<GetCarResponse> responseObserver) {
    final CarDO carDO = this.carUseCase.getCar(request.getId());
    responseObserver.onNext(this.carGrpcMapper.mapCarGrpc(carDO));
    responseObserver.onCompleted();
  }

  @Override
  public void searchCar(final SearchCarRequest request, final StreamObserver<SearchCarResponse> responseObserver) {
    this.carUseCase.search(Context.current(), request.getBrand(), carDO -> {
      Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
      responseObserver.onNext(this.carGrpcMapper.mapSearchCarGrpc(carDO));
    });
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<UploadCarImageRequest> uploadCarImage(final StreamObserver<UploadCarImageResponse> responseObserver) {
    return new UploadCarImageStreamObserver(responseObserver, this.carUseCase);
  }
}
