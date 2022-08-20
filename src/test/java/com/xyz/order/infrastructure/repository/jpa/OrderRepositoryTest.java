package com.xyz.order.infrastructure.repository.jpa;

import com.xyz.order.AbstractContainerBaseTest;
import com.xyz.order.domain.model.Order;
import com.xyz.order.domain.model.OrderItem;
import com.xyz.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class OrderRepositoryTest extends AbstractContainerBaseTest {

  @Autowired private OrderRepository orderRepository;

  @Test
  void shouldReturnOrder_WhenSavedToDB() {
    // given
    var order = Order.create(OrderItem.of(BigDecimal.valueOf(10)));

    // when & then
    assertNotNull(orderRepository.save(order));
  }
}
