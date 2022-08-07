package com.xyz.payment.application;

import com.xyz.payment.domain.command.AddPaymentItemCommand;
import com.xyz.payment.domain.command.CompletePaymentCommand;
import com.xyz.payment.domain.command.CreatePaymentCommand;
import com.xyz.payment.domain.exception.PaymentNotFoundException;
import com.xyz.payment.domain.model.Payment;
import com.xyz.payment.domain.model.PaymentItem;
import com.xyz.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
  private final PaymentRepository paymentRepository;

  public Payment createPayment(CreatePaymentCommand command) {
    final var payment = Payment.create(PaymentItem.of(command.getAmount()));
    return paymentRepository.savePayment(payment);
  }

  public Payment addPaymentItem(AddPaymentItemCommand command) {
    final var payment = findPayment(command.getPaymentId());
    payment.addItem(PaymentItem.of(command.getAmount()));
    return paymentRepository.savePayment(payment);
  }

  public Payment completePayment(CompletePaymentCommand command) {
    final var payment = findPayment(command.getPaymentId());
    payment.complete();
    return paymentRepository.savePayment(payment);
  }

  private Payment findPayment(Long command) {
    return paymentRepository
        .findById(command)
        .orElseThrow(() -> new PaymentNotFoundException("Payment ID Not found :" + command));
  }
}
