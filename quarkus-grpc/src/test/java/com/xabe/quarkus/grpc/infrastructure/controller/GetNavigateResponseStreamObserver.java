package com.xabe.quarkus.grpc.infrastructure.controller;

import com.google.common.util.concurrent.Uninterruptibles;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarRequestOuterClass.GetNavigateCarRequest;
import com.xabe.car.api.grpc.getnavigatecar.GetNavigateCarResponseOuterClass.GetNavigateCarResponse;
import io.grpc.stub.StreamObserver;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GetNavigateResponseStreamObserver implements StreamObserver<GetNavigateCarResponse> {

  private StreamObserver<GetNavigateCarRequest> requestStreamObserver;

  private CompletableFuture<Object> completableFuture;

  private final String id;

  public GetNavigateResponseStreamObserver(final String id) {
    this.id = id;
  }

  @Override
  public void onNext(final GetNavigateCarResponse tripResponse) {
    if (tripResponse.getRemainingDistance() > 0) {
      print(tripResponse);
      this.drive();
    } else {
      this.requestStreamObserver.onCompleted();
    }
  }

  @Override
  public void onError(final Throwable throwable) {
    this.completableFuture.completeExceptionally(throwable);
  }

  @Override
  public void onCompleted() {
    System.out.println("Trip Completed");
    this.completableFuture.complete(new Object());
  }

  public CompletableFuture<Object> startTrip(final StreamObserver<GetNavigateCarRequest> requestStreamObserver) {
    this.requestStreamObserver = requestStreamObserver;
    this.drive();
    this.completableFuture = new CompletableFuture<>();
    return this.completableFuture;
  }

  private void drive() {
    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    final GetNavigateCarRequest tripRequest =
        GetNavigateCarRequest.newBuilder().setId(this.id).setDistanceTravelled(ThreadLocalRandom.current().nextInt(1, 10)).build();
    this.requestStreamObserver.onNext(tripRequest);
  }

  private static void print(final GetNavigateCarResponse tripResponse) {
    System.out.println(LocalTime.now() + ": Remaining Distance : " + tripResponse.getRemainingDistance());
    System.out.println(LocalTime.now() + ": Time To Reach (sec): " + tripResponse.getTimeToDestination());
    System.out.println("------------------------------");
  }

}
