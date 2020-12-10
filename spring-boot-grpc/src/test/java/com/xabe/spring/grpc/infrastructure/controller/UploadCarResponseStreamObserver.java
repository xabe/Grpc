package com.xabe.spring.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.uploadcar.UploadCarImageResponseOuterClass.UploadCarImageResponse;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;

public class UploadCarResponseStreamObserver implements StreamObserver<UploadCarImageResponse> {

  private final CompletableFuture<Integer> completableFuture;

  private Integer size;

  public UploadCarResponseStreamObserver() {
    this.completableFuture = new CompletableFuture<>();
  }

  @Override
  public void onNext(final UploadCarImageResponse uploadCarImageResponse) {
    this.size = uploadCarImageResponse.getSize().getValue();
  }

  @Override
  public void onError(final Throwable throwable) {
    this.completableFuture.completeExceptionally(throwable);
  }

  @Override
  public void onCompleted() {
    this.completableFuture.complete(this.size);
  }

  public CompletableFuture<Integer> getCompletableFuture() {
    return this.completableFuture;
  }
}
