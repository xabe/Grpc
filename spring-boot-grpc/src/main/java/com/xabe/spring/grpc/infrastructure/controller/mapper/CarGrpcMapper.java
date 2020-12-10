package com.xabe.spring.grpc.infrastructure.controller.mapper;

import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.car.api.grpc.searchcar.SearchCarResponseOuterClass.SearchCarResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface CarGrpcMapper {

  GetCarResponse mapCarGrpc(CarDO carDO);

  SearchCarResponse mapSearchCarGrpc(CarDO carDO);
}
