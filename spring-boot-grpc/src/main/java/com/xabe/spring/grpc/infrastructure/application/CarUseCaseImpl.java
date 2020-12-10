package com.xabe.spring.grpc.infrastructure.application;

import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.domain.exception.NotFoundException;
import com.xabe.spring.grpc.domain.repository.CarRepository;
import io.grpc.Context;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarUseCaseImpl implements CarUseCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(CarUseCaseImpl.class);

  private final CarRepository carRepository;

  @Autowired
  public CarUseCaseImpl(final CarRepository carRepository) {
    this.carRepository = carRepository;
  }

  @Override
  public CarDO getCar(final String id) {
    return this.carRepository.findCarById(id).orElseThrow(() -> new NotFoundException("Not found car with id : " + id));
  }

  @Override
  public void search(final Context ctx, final String filterBrand, final Consumer<CarDO> consumerStream) {
    if (ctx.isCancelled()) {
      LOGGER.info("context is cancelled");
      return;
    }
    this.carRepository.getAllCar().stream().filter(item -> filterBrand.equalsIgnoreCase(item.getBrand())).forEach(consumerStream::accept);
  }
}
