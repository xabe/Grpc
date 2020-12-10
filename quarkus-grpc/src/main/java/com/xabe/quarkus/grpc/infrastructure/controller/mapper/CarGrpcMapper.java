package com.xabe.quarkus.grpc.infrastructure.controller.mapper;

import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.quarkus.grpc.domain.entity.CarDO;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface CarGrpcMapper {

  GetCarResponse mapCarGrpc(CarDO carDO);
}
