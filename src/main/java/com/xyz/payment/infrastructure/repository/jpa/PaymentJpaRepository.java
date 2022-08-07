package com.xyz.payment.infrastructure.repository.jpa;

import com.xyz.payment.domain.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentJpaRepository extends CrudRepository<Payment, Long> {}
