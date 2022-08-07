package com.xyz.payment.domain.repository;

import com.xyz.payment.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {
  Optional<Payment> findById(Long paymentId);

  Payment savePayment(Payment payment);
}
