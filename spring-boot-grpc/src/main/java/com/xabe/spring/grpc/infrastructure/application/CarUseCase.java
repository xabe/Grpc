package com.xabe.spring.grpc.infrastructure.application;

import com.xabe.spring.grpc.domain.entity.CarDO;
import io.grpc.Context;
import java.util.function.Consumer;

public interface CarUseCase {

  CarDO getCar(String id);

  void search(Context ctx, String filterBrand, Consumer<CarDO> consumerStream);
}
