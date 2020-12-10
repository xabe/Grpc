package com.xabe.spring.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.searchcar.SearchCarResponseOuterClass.SearchCarResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import io.grpc.stub.StreamObserver;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchCarResponseStreamObserver implements StreamObserver<SearchCarResponse> {

  private final CompletableFuture<List<CarDO>> completableFuture;

  private final List<CarDO> result;

  public SearchCarResponseStreamObserver() {
    this.completableFuture = new CompletableFuture<>();
    this.result = new LinkedList<>();
  }

  @Override
  public void onCompleted() {
    this.completableFuture.complete(this.result);
  }

  @Override
  public void onError(final Throwable throwable) {
    this.completableFuture.completeExceptionally(throwable);
  }

  @Override
  public void onNext(final SearchCarResponse searchCarResponse) {
    this.result
        .add(CarDO.builder().brand(searchCarResponse.getBrand()).model(searchCarResponse.getModel()).id(searchCarResponse.getId()).build());
  }

  public CompletableFuture<List<CarDO>> getCompletableFuture() {
    return this.completableFuture;
  }
}
