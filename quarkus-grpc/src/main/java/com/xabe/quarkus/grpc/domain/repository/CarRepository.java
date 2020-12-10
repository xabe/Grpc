package com.xabe.quarkus.grpc.domain.repository;

import com.xabe.quarkus.grpc.domain.entity.CarDO;
import java.util.Optional;

public interface CarRepository {

  Optional<CarDO> findCarById(String id);
}
