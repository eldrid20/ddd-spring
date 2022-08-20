package com.xyz.order.domain.repository;

import com.xyz.order.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {
  Optional<Order> findById(Long orderId);

  Order save(Order order);
}
