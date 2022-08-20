package com.xyz.order.infrastructure.repository.jpa;

import com.xyz.order.domain.model.Order;
import com.xyz.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryJpaImpl implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;

  @Override
  public Optional<Order> findById(Long orderId) {
    return orderJpaRepository.findById(orderId);
  }

  @Override
  public Order save(Order order) {
    return orderJpaRepository.save(order);
  }
}
