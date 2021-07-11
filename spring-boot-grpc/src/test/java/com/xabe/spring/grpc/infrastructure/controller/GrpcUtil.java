package com.xabe.spring.grpc.infrastructure.controller;

import com.xabe.car.api.grpc.CarEndPointGrpc;
import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointBlockingStub;
import com.xabe.car.api.grpc.CarEndPointGrpc.CarEndPointStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcUtil {

  private static final GrpcUtil INSTANCE = new GrpcUtil();

  private final CarEndPointBlockingStub carEndPointBlockingStub;

  private final CarEndPointStub carEndPointStub;

  private GrpcUtil() {
    try {
      final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("0.0.0.0", 6566).usePlaintext().build();
      this.carEndPointBlockingStub = CarEndPointGrpc.newBlockingStub(managedChannel);
      this.carEndPointStub = CarEndPointGrpc.newStub(managedChannel);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static GrpcUtil instance() {
    return INSTANCE;
  }

  public CarEndPointBlockingStub getCarEndPointBlockingStub() {
    return this.carEndPointBlockingStub;
  }

  public CarEndPointStub getCarEndPointStub() {
    return this.carEndPointStub;
  }
}
