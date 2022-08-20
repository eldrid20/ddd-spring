package com.xyz.order.adapter.incoming.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.order.adapter.incoming.rest.dto.OrderItemDto;
import com.xyz.order.application.OrderService;
import com.xyz.order.domain.command.AddOrderItemCommand;
import com.xyz.order.domain.command.CompleteOrderCommand;
import com.xyz.order.domain.command.CreateOrderCommand;
import com.xyz.order.domain.model.Order;
import com.xyz.order.domain.model.OrderItem;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerMvcTest {

  private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.00);

  private static final Long PAYMENT_ID = 1L;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;
  @MockBean private OrderService orderService;

  @NotNull
  private static OrderItemDto getOrderDto() {
    final var orderDto = new OrderItemDto();
    orderDto.setAmount(AMOUNT);
    return orderDto;
  }

  @NotNull
  private static Order getExpectedOrderResponse() {
    final var order = Order.create(OrderItem.of(AMOUNT));
    order.setId(1L);
    return order;
  }

  private static Order getExpectedOrderResponseWithAdditionalItem(OrderItem orderItem) {
    final var order = Order.create(OrderItem.of(AMOUNT));
    order.setId(1L);
    order.addItem(orderItem);
    return order;
  }

  private static Order getCompletedOrder() {
    final var order = Order.create(OrderItem.of(AMOUNT));
    order.setId(1L);
    order.complete();
    return order;
  }

  @Test
  void createOrder_WhenSuccessful_ShouldReturnHttp200() throws Exception {
    // given
    final var createOrder = CreateOrderCommand.of(AMOUNT);
    final var orderDto = getOrderDto();

    final var expectedOrderResponse = getExpectedOrderResponse();
    when(orderService.create(createOrder)).thenReturn(expectedOrderResponse);

    // when
    final var response =
        this.mockMvc
            .perform(
                post("/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isOk())
            .andReturn();

    // then
    final var orderResponse = response.getResponse().getContentAsString();
    assertThat(objectMapper.writeValueAsString(expectedOrderResponse)).isEqualTo(orderResponse);
  }

  @Test
  void addOrderItem_WhenSuccessful_ShouldReturnHttp200() throws Exception {
    // given
    final var orderDto = getOrderDto();
    final var addOrderItemCommand = AddOrderItemCommand.of(PAYMENT_ID, AMOUNT);
    final var expectedOrderResponse =
        getExpectedOrderResponseWithAdditionalItem(
            OrderItem.of(addOrderItemCommand.getAmount()));
    when(orderService.addItem(addOrderItemCommand)).thenReturn(expectedOrderResponse);

    // when
    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + PAYMENT_ID + "/items")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isOk())
            .andReturn();

    // then
    final var orderResponse = response.getResponse().getContentAsString();
    assertThat(objectMapper.writeValueAsString(expectedOrderResponse)).isEqualTo(orderResponse);
  }

  @Test
  void completeOrder_WhenSuccessful_ShouldReturnHttp200() throws Exception {
    // given
    final var orderDto = getOrderDto();
    final var completeOrderCommand = CompleteOrderCommand.of(PAYMENT_ID);
    final var expectedOrderResponse = getCompletedOrder();
    when(orderService.complete(completeOrderCommand))
        .thenReturn(expectedOrderResponse);

    // when
    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + PAYMENT_ID + "/complete")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isOk())
            .andReturn();

    // then
    final var orderResponse = response.getResponse().getContentAsString();
    assertThat(objectMapper.writeValueAsString(expectedOrderResponse)).isEqualTo(orderResponse);
  }
}
