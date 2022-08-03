package com.xyz.payment.application;

import com.xyz.payment.AbstractContainerBaseTest;
import com.xyz.payment.domain.command.AddPaymentItemCommand;
import com.xyz.payment.domain.command.CompletePaymentCommand;
import com.xyz.payment.domain.command.CreatePaymentCommand;
import com.xyz.payment.domain.exception.PaymentNotFoundException;
import com.xyz.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class PaymentProcessorTest extends AbstractContainerBaseTest {

    @Autowired
    private PaymentProcessor paymentProcessor;

    @Test
    void createPaymentByCommand_ShouldCreatedSuccessfully() {
        //given
        final var command = CreatePaymentCommand.of(BigDecimal.valueOf(10.00));

        //when
        final var payment = paymentProcessor.createPayment(command);

        //then
        assertThat(payment.getTotalAmount().compareTo(BigDecimal.valueOf(10.00))).isZero();
    }

    @Test
    void addPaymentItemByCommand_ShouldBeAddedSuccessfully() {
        //given
        final var createPaymentCommand = CreatePaymentCommand.of(BigDecimal.valueOf(10.00));
        final var payment = paymentProcessor.createPayment(createPaymentCommand);

        //when
        final var addItemCommand = AddPaymentItemCommand.of(payment.getId(),BigDecimal.valueOf(35.00));
        final var updatedPayment =
                paymentProcessor.addPaymentItem(addItemCommand);

        //then
        assertThat(updatedPayment.getTotalAmount().compareTo(BigDecimal.valueOf(45.00))).isZero();
    }

    @Test
    void addPaymentItemByCommand_WhenInvalidPaymentIdSupplied_ShouldThrowPaymentNotFoundException() {
        //given
        final var addPaymentItemCommand = AddPaymentItemCommand.of(-1L,BigDecimal.valueOf(12));
        Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentProcessor.addPaymentItem(addPaymentItemCommand));
    }

    @Test
    void completePaymentByCommand_ShouldBeProceedSuccessfully() {
        //given
        final var createPaymentCommand = CreatePaymentCommand.of(BigDecimal.valueOf(10.00));
        final var payment = paymentProcessor.createPayment(createPaymentCommand);

        //when
        final var addItemCommand = AddPaymentItemCommand.of(payment.getId(),BigDecimal.valueOf(35.00));
        final var updatedPayment = paymentProcessor.addPaymentItem(addItemCommand);

        final var completeCommand = CompletePaymentCommand.of(updatedPayment.getId());
        paymentProcessor.completePayment(completeCommand);

        //then
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void completePaymentByCommand_WhenInvalidPaymentIdSupplied_ShouldThrowPaymentNotFoundException() {
        //given
        final var completeCommand = CompletePaymentCommand.of(-1L);
        Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentProcessor.completePayment(completeCommand));
    }
}