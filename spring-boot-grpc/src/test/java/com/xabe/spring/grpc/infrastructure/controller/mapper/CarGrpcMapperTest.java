package com.xabe.spring.grpc.infrastructure.controller.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.car.api.grpc.getcar.GetCarResponseOuterClass.GetCarResponse;
import com.xabe.spring.grpc.domain.entity.CarDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarGrpcMapperTest {

  private CarGrpcMapper carGrpcMapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.carGrpcMapper = new CarGrpcMapperImpl();
  }

  @Test
  public void givenACarDOWhenInvokeMapGrpcThenReturnGetCarResponse() throws Exception {
    final CarDO carDO = CarDO.builder().id("id").model("name").brand("brand").build();

    final GetCarResponse result = this.carGrpcMapper.mapCarGrpc(carDO);

    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is("id"));
    assertThat(result.getModel(), is("name"));
    assertThat(result.getBrand(), is("brand"));
  }

}