package com.xabe.quarkus.grpc.infrastructure.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.domain.repository.CarRepository;
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

}