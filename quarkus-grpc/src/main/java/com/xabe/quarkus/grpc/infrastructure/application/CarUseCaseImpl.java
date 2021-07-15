package com.xabe.quarkus.grpc.infrastructure.application;

import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.domain.exception.NotFoundException;
import com.xabe.quarkus.grpc.domain.repository.CarRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarUseCaseImpl implements CarUseCase {

  private final CarRepository carRepository;

  CarUseCaseImpl(final CarRepository carRepository) {
    this.carRepository = carRepository;
  }

  @Override
  public CarDO getCar(final String id) {
    return this.carRepository.findCarById(id).orElseThrow(() -> new NotFoundException("Not found car with id : " + id));
  }
}
