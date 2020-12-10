package com.xabe.spring.grpc.domain.repository;

import com.xabe.spring.grpc.domain.entity.CarDO;
import java.util.Collection;
import java.util.Optional;

public interface CarRepository {

  Optional<CarDO> findCarById(String id);

  Collection<CarDO> getAllCar();
}
