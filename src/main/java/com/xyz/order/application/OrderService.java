package com.xyz.order.application;

import com.xyz.order.domain.command.AddOrderItemCommand;
import com.xyz.order.domain.command.CompleteOrderCommand;
import com.xyz.order.domain.command.CreateOrderCommand;
import com.xyz.order.domain.exception.OrderNotFoundException;
import com.xyz.order.domain.model.Order;
import com.xyz.order.domain.model.OrderItem;
import com.xyz.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
  private final OrderRepository orderRepository;

  public Order create(CreateOrderCommand command) {
    final var order = Order.create(OrderItem.of(command.getAmount()));
    return orderRepository.save(order);
  }

  public Order addItem(AddOrderItemCommand command) {
    final var order = findOrder(command.getOrderId());
    order.addItem(OrderItem.of(command.getAmount()));
    return orderRepository.save(order);
  }

  public Order complete(CompleteOrderCommand command) {
    final var order = findOrder(command.getOrderId());
    order.complete();
    return orderRepository.save(order);
  }

  private Order findOrder(Long command) {
    return orderRepository
        .findById(command)
        .orElseThrow(() -> new OrderNotFoundException("Order ID Not found :" + command));
  }
}
