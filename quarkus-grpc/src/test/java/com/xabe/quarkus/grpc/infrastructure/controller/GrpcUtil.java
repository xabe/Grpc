package com.xabe.quarkus.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.CarEndPointGrpc;
import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointBlockingStub;
import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class GrpcUtil {

  private final CarEndPointBlockingStub carEndPointBlockingStub;

  private final CarEndPointStub carEndPointStub;

  private final ManagedChannel managedChannel;

  GrpcUtil() {
    try {
      this.managedChannel = ManagedChannelBuilder.forAddress("0.0.0.0", 6566).usePlaintext().build();
      this.carEndPointBlockingStub = CarEndPointGrpc.newBlockingStub(this.managedChannel);
      this.carEndPointStub = CarEndPointGrpc.newStub(this.managedChannel);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public CarEndPointBlockingStub getCarEndPointBlockingStub() {
    return this.carEndPointBlockingStub;
  }

  public CarEndPointStub getCarEndPointStub() {
    return this.carEndPointStub;
  }

  public void shutdown() throws InterruptedException {
    this.managedChannel.shutdown();
    this.managedChannel.awaitTermination(10, TimeUnit.SECONDS);
  }
}
