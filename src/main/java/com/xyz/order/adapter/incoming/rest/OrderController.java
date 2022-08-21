package com.xyz.order.adapter.incoming.rest;

import com.xyz.order.adapter.incoming.rest.dto.OrderItemDto;
import com.xyz.order.application.OrderService;
import com.xyz.order.domain.command.AddOrderItem;
import com.xyz.order.domain.command.CompleteOrder;
import com.xyz.order.domain.command.CreateOrder;
import com.xyz.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping(
      value = "/v1/orders",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Order createOrder(@RequestBody @Valid OrderItemDto orderItemDto) {
    return orderService.create(CreateOrder.of(orderItemDto.getAmount()));
  }

  @PutMapping(
      value = "/v1/orders/{orderId}/items",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Order addItem(
          @PathVariable Long orderId, @RequestBody @Valid OrderItemDto orderItemDto) {
    return orderService.addItem(
        AddOrderItem.of(orderId, orderItemDto.getAmount()));
  }

  @PutMapping(
      value = "/v1/orders/{orderId}/complete",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Order completeOrder(@PathVariable Long orderId) {
    return orderService.complete(CompleteOrder.of(orderId));
  }
}
