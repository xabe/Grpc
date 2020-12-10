package com.xabe.spring.grpc.infrastructure.persistence;

import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.domain.repository.CarRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCarRepository implements CarRepository {

  private final Map<String, CarDO> carDOMap;

  public InMemoryCarRepository() {
    this.carDOMap =
        Map.of(
            "1", CarDO.builder().id("1").model("Mazda3").brand("Mazda").build(),
            "2", CarDO.builder().id("2").model("CX-5").brand("Mazda").build(),
            "3", CarDO.builder().id("3").model("Civic").brand("Honda").build());
  }

  @Override
  public Optional<CarDO> findCarById(final String id) {
    return Optional.ofNullable(this.carDOMap.get(id));
  }

  @Override
  public Collection<CarDO> getAllCar() {
    return this.carDOMap.values();
  }
}
