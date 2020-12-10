package com.xabe.spring.grpc.infrastructure.application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.spring.grpc.domain.entity.CarDO;
import com.xabe.spring.grpc.domain.exception.NotFoundException;
import com.xabe.spring.grpc.domain.repository.CarRepository;
import io.grpc.Context;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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

  @Test
  public void givenAContextAndFilterAndConsumerWhenInvokeSearchThenReturnAllCarDO() throws Exception {
    final Context context = spy(Context.current());
    final String filter = "brand";
    final Consumer consumer = mock(Consumer.class);

    when(context.isCancelled()).thenReturn(false);
    when(this.carRepository.getAllCar()).thenReturn(List.of(CarDO.builder().brand(filter).build()));

    this.carUseCase.search(context, filter, consumer);

    verify(consumer).accept(any());
  }

  @Test
  public void givenAContextAndFilterAndConsumerWhenInvokeSearchThenReturnEmpty() throws Exception {
    final Context context = spy(Context.current());
    final String filter = "brand";
    final Consumer consumer = mock(Consumer.class);

    when(context.isCancelled()).thenReturn(true);
    when(this.carRepository.getAllCar()).thenReturn(List.of(CarDO.builder().brand(filter).build()));

    this.carUseCase.search(context, filter, consumer);

    verify(consumer, never()).accept(any());
  }

}