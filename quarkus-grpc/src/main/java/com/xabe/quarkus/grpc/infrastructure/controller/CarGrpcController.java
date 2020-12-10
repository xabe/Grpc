package com.xabe.quarkus.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointImplBase;
import com.xabe.car.api.grpc.getcar.GetCarRequestOuterClass.GetCarRequest;
import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarRequestOuterClass.GetNavigateCarRequest;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarResponseOuterClass.GetNavigateCarResponse;
import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.infrastructure.application.CarUseCase;
import com.xabe.quarkus.grpc.infrastructure.controller.mapper.CarGrpcMapper;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CarGrpcController extends CarEndPointImplBase {

  @Inject
  CarUseCase carUseCase;

  @Inject
  CarGrpcMapper carGrpcMapper;

  public CarGrpcController() {
  }

  CarGrpcController(final CarUseCase carUseCase, final CarGrpcMapper carGrpcMapper) {
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
  public StreamObserver<GetNavigateCarRequest> getNavigateCar(final StreamObserver<GetNavigateCarResponse> responseObserver) {
    return new GetNavigateCarRequestObserver(responseObserver, this.carUseCase);
  }
}
