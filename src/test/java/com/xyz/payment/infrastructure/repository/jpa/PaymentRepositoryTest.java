package com.xyz.payment.infrastructure.repository.jpa;

import com.xyz.payment.AbstractContainerBaseTest;
import com.xyz.payment.domain.model.Payment;
import com.xyz.payment.domain.model.PaymentItem;
import com.xyz.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PaymentRepositoryTest extends AbstractContainerBaseTest {

  @Autowired private PaymentRepository paymentRepository;

  @Test
  void shouldReturnPayment_WhenSavedToDB() {
    // given
    var payment = Payment.create(PaymentItem.of(BigDecimal.valueOf(10)));

    // when & then
    assertNotNull(paymentRepository.savePayment(payment));
  }
}
