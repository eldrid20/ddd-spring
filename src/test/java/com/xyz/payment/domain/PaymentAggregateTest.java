package com.xyz.payment.domain;

import com.xyz.payment.domain.exception.InvalidPaymentStateException;
import com.xyz.payment.domain.model.Payment;
import com.xyz.payment.domain.model.PaymentItem;
import com.xyz.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentAggregateTest {

    @Test
    void shouldCompleteOrderSuccessfully_WhenSetupDataAndStateIsValid() {
        //given
        final Payment payment = Payment.create(PaymentItem.of(BigDecimal.valueOf(23.0)));

        //when
        payment.complete();

        //then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals(BigDecimal.valueOf(23.0), payment.getTotalAmount());
    }

    @Test
    void shouldThrowInvalidOrderStateException_WhenOrderInWrongState() {
        //given
        final Payment payment = Payment.create(PaymentItem.of(BigDecimal.valueOf(23.0)));
        payment.complete();

        //when
        var invalidOrderStateException = Assertions.assertThrows(InvalidPaymentStateException.class, payment::complete);

        //then
        assertEquals("Order has invalid status:COMPLETED",invalidOrderStateException.getMessage());
    }

    @Test
    void shouldCompletePaymentSuccessfully() {
        //given
        final Payment payment = Payment.create(PaymentItem.of(BigDecimal.valueOf(23.00)));
        payment.addItem(PaymentItem.of(BigDecimal.valueOf(32.00)));

        //when
        payment.complete();

        //then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertEquals(BigDecimal.valueOf(55.00), payment.getTotalAmount());
    }

}