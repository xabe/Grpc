package com.xabe.quarkus.grpc.infrastructure.persistence;

import com.xabe.quarkus.grpc.domain.entity.CarDO;
import com.xabe.quarkus.grpc.domain.repository.CarRepository;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InMemoryCarRepository implements CarRepository {

  private final Map<String, CarDO> carDOMap;

  public InMemoryCarRepository() {
    this.carDOMap = Map.of("1", CarDO.builder().id("1").name("Mazda").build());
  }

  @Override
  public Optional<CarDO> findCarById(final String id) {
    return Optional.ofNullable(this.carDOMap.get(id));
  }
}
