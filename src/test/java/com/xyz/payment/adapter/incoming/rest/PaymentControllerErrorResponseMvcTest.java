package com.xyz.payment.adapter.incoming.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.payment.adapter.incoming.rest.dto.ErrorField;
import com.xyz.payment.adapter.incoming.rest.dto.ErrorFieldResponseDto;
import com.xyz.payment.adapter.incoming.rest.dto.ErrorResponseDto;
import com.xyz.payment.adapter.incoming.rest.dto.PaymentDto;
import com.xyz.payment.application.PaymentProcessor;
import com.xyz.payment.domain.command.CompletePaymentCommand;
import com.xyz.payment.domain.exception.PaymentNotFoundException;
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


@WebMvcTest(PaymentController.class)
class PaymentControllerErrorResponseMvcTest {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10.00);

    private static final Long PAYMENT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentProcessor processor;

    @Test
    void createPayment_WhenAmountEmpty_ShouldReturnHttp400() throws Exception {
        //given
        final var paymentDto = getPaymentDto();
        paymentDto.setAmount(null);

        //when
        final var response = this.mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        final var expectedResponse = new ErrorFieldResponseDto();
        expectedResponse.setErrorMessage("Bad Request");
        expectedResponse.setErrorFields(List.of(new ErrorField("amount","must not be null")));
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(paymentResponse);
    }


    @Test
    void addPaymentItem_WhenAmountEmpty_ShouldReturnHttp400() throws Exception {
        //given
        final var paymentDto = getPaymentDto();
        paymentDto.setAmount(null);

        //when
        final var response = this.mockMvc.perform(put("/payments/"+PAYMENT_ID+"/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        final var expectedResponse = new ErrorFieldResponseDto();
        expectedResponse.setErrorMessage("Bad Request");
        expectedResponse.setErrorFields(List.of(new ErrorField("amount","must not be null")));
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(paymentResponse);
    }

    @Test
    void completePaymentItem_WhenPaymentIdNotFound_ShouldReturnHttp404() throws Exception {
        //given & when
        Mockito.when(processor.completePayment(CompletePaymentCommand.of(PAYMENT_ID)))
                .thenThrow(new PaymentNotFoundException("Payment ID :"+PAYMENT_ID+" not found"));

        final var response = this.mockMvc.perform(put("/payments/"+PAYMENT_ID+"/complete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        final var paymentResponse = response.getResponse().getContentAsString();
        final var expectedResponse = new ErrorResponseDto();
        expectedResponse.setErrorMessage("Payment ID :"+PAYMENT_ID+" not found");
        assertThat(objectMapper.writeValueAsString(expectedResponse)).isEqualTo(paymentResponse);
    }

    @NotNull
    private static PaymentDto getPaymentDto() {
        final var paymentDto = new PaymentDto();
        paymentDto.setAmount(AMOUNT);
        return paymentDto;
    }
}