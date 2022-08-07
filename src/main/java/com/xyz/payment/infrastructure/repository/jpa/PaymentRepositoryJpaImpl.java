package com.xyz.payment.infrastructure.repository.jpa;

import com.xyz.payment.domain.model.Payment;
import com.xyz.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryJpaImpl implements PaymentRepository {

  private final PaymentJpaRepository paymentJpaRepository;

  @Override
  public Optional<Payment> findById(Long paymentId) {
    return paymentJpaRepository.findById(paymentId);
  }

  @Override
  public Payment savePayment(Payment payment) {
    return paymentJpaRepository.save(payment);
  }
}
