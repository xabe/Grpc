package com.xabe.quarkus.grpc.infrastructure.application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.domain.exception.NotFoundException;
import com.xabe.quarkus.grpc.domain.repository.CarRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarUseCaseImplTest {

  private CarRepository carRepository;

  private CarUseCase carUseCase;

  @BeforeEach
  public void setUp() throws Exception {
    this.carRepository = mock(CarRepository.class);
    this.carUseCase = new CarUseCaseImpl(this.carRepository);
  }

  @Test
  public void givenACarIdWhenInvokeGetCarThenReturnCarDO() throws Exception {
    final String id = "id";

    when(this.carRepository.findCarById(eq(id))).thenReturn(Optional.of(CarDO.builder().build()));

    final CarDO result = this.carUseCase.getCar(id);

    assertThat(result, is(notNullValue()));
  }

  @Test
  public void givenACarIdWhenInvokeGetCarThenReturnExceptionNotFound() throws Exception {
    final String id = "id";

    when(this.carRepository.findCarById(eq(id))).thenReturn(Optional.empty());

    Assertions.assertThrows(NotFoundException.class, () -> this.carUseCase.getCar(id));
  }

}