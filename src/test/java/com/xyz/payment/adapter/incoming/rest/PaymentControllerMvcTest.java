package com.xyz.payment.adapter.incoming.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.payment.adapter.incoming.rest.dto.PaymentDto;
import com.xyz.payment.application.PaymentProcessor;
import com.xyz.payment.domain.command.AddPaymentItemCommand;
import com.xyz.payment.domain.command.CompletePaymentCommand;
import com.xyz.payment.domain.command.CreatePaymentCommand;
import com.xyz.payment.domain.model.Payment;
import com.xyz.payment.domain.model.PaymentItem;
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


@WebMvcTest(PaymentController.class)
class PaymentControllerMvcTest {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.00);

    private static final Long PAYMENT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PaymentProcessor processor;

    @Test
    void createPayment_WhenSuccessful_ShouldReturnHttp200() throws Exception {
        //given
        final var createPayment = CreatePaymentCommand.of(AMOUNT);
        final var paymentDto = getPaymentDto();

        final var expectedPaymentResponse = getExpectedPaymentResponse();
        when(processor.createPayment(createPayment)).thenReturn(expectedPaymentResponse);

        //when
        final var response = this.mockMvc.perform(post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedPaymentResponse)).isEqualTo(paymentResponse);
    }

    @Test
    void addPaymentItem_WhenSuccessful_ShouldReturnHttp200() throws Exception {
        //given
        final var paymentDto = getPaymentDto();
        final var addPaymentItemCommand = AddPaymentItemCommand.of(PAYMENT_ID,AMOUNT);
        final var expectedPaymentResponse = getExpectedPaymentResponseWithAdditionalItem(PaymentItem.of(addPaymentItemCommand.getAmount()));
        when(processor.addPaymentItem(addPaymentItemCommand)).thenReturn(expectedPaymentResponse);

        //when
        final var response = this.mockMvc.perform(put("/v1/payments/"+PAYMENT_ID+"/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedPaymentResponse)).isEqualTo(paymentResponse);
    }

    @Test
    void completePayment_WhenSuccessful_ShouldReturnHttp200() throws Exception {
        //given
        final var paymentDto = getPaymentDto();
        final var completePaymentCommand = CompletePaymentCommand.of(PAYMENT_ID);
        final var expectedPaymentResponse = getCompletedPayment();
        when(processor.completePayment(completePaymentCommand)).thenReturn(expectedPaymentResponse);

        //when
        final var response = this.mockMvc.perform(put("/v1/payments/"+PAYMENT_ID+"/complete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        assertThat(objectMapper.writeValueAsString(expectedPaymentResponse)).isEqualTo(paymentResponse);
    }

    @NotNull
    private static PaymentDto getPaymentDto() {
        final var paymentDto = new PaymentDto();
        paymentDto.setAmount(AMOUNT);
        return paymentDto;
    }

    @NotNull
    private static Payment getExpectedPaymentResponse() {
        final var payment = Payment.create(PaymentItem.of(AMOUNT));
        payment.setId(1L);
        return payment;
    }

    private static Payment getExpectedPaymentResponseWithAdditionalItem(PaymentItem paymentItem) {
        final var payment = Payment.create(PaymentItem.of(AMOUNT));
        payment.setId(1L);
        payment.addItem(paymentItem);
        return payment;
    }

    private static Payment getCompletedPayment() {
        final var payment = Payment.create(PaymentItem.of(AMOUNT));
        payment.setId(1L);
        payment.complete();
        return payment;
    }
}