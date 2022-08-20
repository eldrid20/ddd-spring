package com.xyz.order.domain;

import com.xyz.order.domain.exception.InvalidStateException;
import com.xyz.order.domain.model.Order;
import com.xyz.order.domain.model.OrderItem;
import com.xyz.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderAggregateTest {

  @Test
  void shouldCompleteOrderSuccessfully_WhenSetupDataAndStateIsValid() {
    // given
    final Order order = Order.create(OrderItem.of(BigDecimal.valueOf(23.0)));

    // when
    order.complete();

    // then
    assertEquals(OrderStatus.COMPLETED, order.getStatus());
    assertEquals(BigDecimal.valueOf(23.0), order.getTotalAmount());
  }

  @Test
  void shouldThrowInvalidOrderStateException_WhenOrderInWrongState() {
    // given
    final Order order = Order.create(OrderItem.of(BigDecimal.valueOf(23.0)));
    order.complete();

    // when
    var invalidOrderStateException =
        Assertions.assertThrows(InvalidStateException.class, order::complete);

    // then
    assertEquals("Order has invalid status:COMPLETED", invalidOrderStateException.getMessage());
  }

  @Test
  void shouldCompleteOrderSuccessfully() {
    // given
    final Order order = Order.create(OrderItem.of(BigDecimal.valueOf(23.00)));
    order.addItem(OrderItem.of(BigDecimal.valueOf(32.00)));

    // when
    order.complete();

    // then
    assertEquals(OrderStatus.COMPLETED, order.getStatus());
    assertEquals(BigDecimal.valueOf(55.00), order.getTotalAmount());
  }
}
