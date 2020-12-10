package com.xabe.quarkus.grpc.infrastructure.application;

import com.xabe.quarkus.grpc.domain.entity.CarDO;

public interface CarUseCase {

  CarDO getCar(String id);
}
