package com.xabe.quarkus.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarRequestOuterClass.GetNavigateCarRequest;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarResponseOuterClass.GetNavigateCarResponse;
import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.infrastructure.application.CarUseCase;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetNavigateCarRequestObserver implements StreamObserver<GetNavigateCarRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetNavigateCarRequestObserver.class);

  private final StreamObserver<GetNavigateCarResponse> responseObserver;

  private final int totalDistance = 100;

  private final LocalTime startTime;

  private final CarUseCase carUseCase;

  private int distanceTraveled;

  private String id;

  private CarDO carDO;

  public GetNavigateCarRequestObserver(final StreamObserver<GetNavigateCarResponse> responseObserver, final CarUseCase carUseCase) {
    this.responseObserver = responseObserver;
    this.carUseCase = carUseCase;
    this.startTime = LocalTime.now();
  }

  @Override
  public void onNext(final GetNavigateCarRequest getNavigateCarRequest) {
    if (Objects.isNull(this.carDO)) {
      this.id = getNavigateCarRequest.getId();
      this.carDO = this.carUseCase.getCar(this.id);
    }
    this.carUseCase.getCar(this.id);
    this.distanceTraveled += getNavigateCarRequest.getDistanceTravelled();
    this.distanceTraveled = Math.min(this.totalDistance, this.distanceTraveled);
    final int remainingDistance = Math.max(0, (this.totalDistance - this.distanceTraveled));

    // the client has reached destination
    if (remainingDistance == 0) {
      this.responseObserver.onNext(GetNavigateCarResponse.getDefaultInstance());
      return;
    }

    // client has not yet reached destination
    long elapsedDuration = Duration.between(this.startTime, LocalTime.now()).getSeconds();
    elapsedDuration = elapsedDuration < 1 ? 1 : elapsedDuration;
    final double currentSpeed = (this.distanceTraveled * 1.0d) / elapsedDuration;
    final int timeToReach = (int) (remainingDistance / currentSpeed);

    final GetNavigateCarResponse getNavigateCarResponse = GetNavigateCarResponse.newBuilder()
        .setId(this.id)
        .setRemainingDistance(remainingDistance)
        .setTimeToDestination(timeToReach)
        .setName(this.carDO.getName())
        .build();
    this.responseObserver.onNext(getNavigateCarResponse);
  }

  @Override
  public void onError(final Throwable throwable) {
    LOGGER.error("Error to send client : ", throwable);
  }

  @Override
  public void onCompleted() {
    this.responseObserver.onCompleted();
    LOGGER.info("Client reached safely");
  }
}
