package com.xyz.order.application;

import com.xyz.order.AbstractContainerBaseTest;
import com.xyz.order.domain.command.AddOrderItemCommand;
import com.xyz.order.domain.command.CompleteOrderCommand;
import com.xyz.order.domain.command.CreateOrderCommand;
import com.xyz.order.domain.exception.OrderNotFoundException;
import com.xyz.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class OrderServiceTest extends AbstractContainerBaseTest {

  @Autowired private OrderService orderService;

  @Test
  void createOrderByCommand_ShouldCreatedSuccessfully() {
    // given
    final var command = CreateOrderCommand.of(BigDecimal.valueOf(10.00));

    // when
    final var order = orderService.create(command);

    // then
    assertThat(order.getTotalAmount().compareTo(BigDecimal.valueOf(10.00))).isZero();
  }

  @Test
  void addOrderItemByCommand_ShouldBeAddedSuccessfully() {
    // given
    final var createOrderCommand = CreateOrderCommand.of(BigDecimal.valueOf(10.00));
    final var order = orderService.create(createOrderCommand);

    // when
    final var addItemCommand = AddOrderItemCommand.of(order.getId(), BigDecimal.valueOf(35.00));
    final var updatedOrder = orderService.addItem(addItemCommand);

    // then
    assertThat(updatedOrder.getTotalAmount().compareTo(BigDecimal.valueOf(45.00))).isZero();
  }

  @Test
  void addOrderItemByCommand_WhenInvalidOrderIdSupplied_ShouldThrowOrderNotFoundException() {
    // given
    final var addOrderItemCommand = AddOrderItemCommand.of(-1L, BigDecimal.valueOf(12));
    Assertions.assertThrows(
        OrderNotFoundException.class, () -> orderService.addItem(addOrderItemCommand));
  }

  @Test
  void completeOrderByCommand_ShouldBeProceedSuccessfully() {
    // given
    final var createOrderCommand = CreateOrderCommand.of(BigDecimal.valueOf(10.00));
    final var order = orderService.create(createOrderCommand);

    // when
    final var addItemCommand = AddOrderItemCommand.of(order.getId(), BigDecimal.valueOf(35.00));
    final var updatedOrder = orderService.addItem(addItemCommand);

    final var completeCommand = CompleteOrderCommand.of(updatedOrder.getId());
    orderService.complete(completeCommand);

    // then
    assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }

  @Test
  void completeOrderByCommand_WhenInvalidOrderIdSupplied_ShouldThrowOrderNotFoundException() {
    // given
    final var completeCommand = CompleteOrderCommand.of(-1L);
    Assertions.assertThrows(
        OrderNotFoundException.class, () -> orderService.complete(completeCommand));
  }
}
