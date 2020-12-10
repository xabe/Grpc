package com.xabe.spring.grpc.infrastructure.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.domain.repository.CarRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryCarRepositoryTest {

  private CarRepository carRepository;

  @BeforeEach
  public void setUp() throws Exception {
    this.carRepository = new InMemoryCarRepository();
  }

  @Test
  public void givenACarIdWhenInvokeFindCarByIdThenReturnOptional() throws Exception {
    final String id = "1";

    final Optional<CarDO> result = this.carRepository.findCarById(id);

    assertThat(result, is(notNullValue()));
    assertThat(result.isPresent(), is(true));
  }

  @Test
  public void shouldGetAllCars() throws Exception {
    assertThat(this.carRepository.getAllCar(), is(notNullValue()));
    assertThat(this.carRepository.getAllCar().size(), is(greaterThanOrEqualTo(1)));
  }

}