package com.xyz.order.adapter.incoming.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.order.adapter.incoming.rest.dto.ErrorField;
import com.xyz.order.adapter.incoming.rest.dto.ErrorFieldResponseDto;
import com.xyz.order.adapter.incoming.rest.dto.ErrorResponseDto;
import com.xyz.order.adapter.incoming.rest.dto.OrderItemDto;
import com.xyz.order.application.OrderService;
import com.xyz.order.domain.command.AddOrderItemCommand;
import com.xyz.order.domain.command.CompleteOrderCommand;
import com.xyz.order.domain.exception.InvalidStateException;
import com.xyz.order.domain.exception.OrderNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerErrorResponseMvcTest {

  private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.00);

  private static final Long ORDER_ID = 1L;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private OrderService orderService;

  @NotNull
  private static OrderItemDto getOrderItemDto() {
    final var orderItemDto = new OrderItemDto();
    orderItemDto.setAmount(AMOUNT);
    return orderItemDto;
  }

  @Test
  void createOrder_WhenAmountEmpty_ShouldReturnHttp400() throws Exception {
    // given
    final var orderItemDto = getOrderItemDto();
    orderItemDto.setAmount(null);

    // when
    final var response =
        this.mockMvc
            .perform(
                post("/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderItemDto)))
            .andExpect(status().isBadRequest())
            .andReturn();

    // then
    final var orderResponse = response.getResponse().getContentAsString();
    final var expectedResponse = new ErrorFieldResponseDto();
    expectedResponse.setErrorMessage("Bad Request");
    expectedResponse.setErrorFields(List.of(new ErrorField("amount", "must not be null")));
    assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(orderResponse);
  }

  @Test
  void addOrderItem_WhenAmountEmpty_ShouldReturnHttp400() throws Exception {
    // given
    final var orderItemDto = getOrderItemDto();
    orderItemDto.setAmount(null);

    // when
    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + ORDER_ID + "/items")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderItemDto)))
            .andExpect(status().isBadRequest())
            .andReturn();

    // then
    final var orderItemResponse = response.getResponse().getContentAsString();
    final var expectedResponse = new ErrorFieldResponseDto();
    expectedResponse.setErrorMessage("Bad Request");
    expectedResponse.setErrorFields(List.of(new ErrorField("amount", "must not be null")));
    assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(orderItemResponse);
  }

  @Test
  void addOrderItem_WhenOrderHasBeenCompleted_ShouldReturnHttp409() throws Exception {
    // given & when
    final var orderDto = getOrderItemDto();
    final var expectedErrorMessage =
        String.format("Invalid Status: order ID : %d status is COMPLETED", ORDER_ID);
    Mockito.when(
            orderService.addItem(
                AddOrderItemCommand.of(ORDER_ID, orderDto.getAmount())))
        .thenThrow(new InvalidStateException(expectedErrorMessage));

    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + ORDER_ID + "/items")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isConflict())
            .andReturn();

    // then
    final var orderCompleteResponse = response.getResponse().getContentAsString();
    final var expectedResponse = new ErrorResponseDto();
    expectedResponse.setErrorMessage(expectedErrorMessage);
    assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(orderCompleteResponse);
  }

  @Test
  void completeOrderItem_WhenOrderIdNotFound_ShouldReturnHttp404() throws Exception {
    // given & when
    Mockito.when(orderService.complete(CompleteOrderCommand.of(ORDER_ID)))
        .thenThrow(new OrderNotFoundException("Order ID :" + ORDER_ID + " not found"));

    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + ORDER_ID + "/complete")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andReturn();

    // then
    final var orderCompleteResponse = response.getResponse().getContentAsString();
    final var expectedResponse = new ErrorResponseDto();
    expectedResponse.setErrorMessage("Order ID :" + ORDER_ID + " not found");
    assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(orderCompleteResponse);
  }

  @Test
  void completeOrderItem_WhenOrderHasBeenCompleted_ShouldReturnHttp409() throws Exception {
    // given & when
    final var expectedErrorMessage =
        String.format("Invalid Status: order ID : %d status is COMPLETED", ORDER_ID);
    Mockito.when(orderService.complete(CompleteOrderCommand.of(ORDER_ID)))
        .thenThrow(new InvalidStateException(expectedErrorMessage));

    final var response =
        this.mockMvc
            .perform(
                put("/v1/orders/" + ORDER_ID + "/complete")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isConflict())
            .andReturn();

    // then
    final var orderCompleteResponse = response.getResponse().getContentAsString();
    final var expectedResponse = new ErrorResponseDto();
    expectedResponse.setErrorMessage(expectedErrorMessage);
    assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(orderCompleteResponse);
  }
}
