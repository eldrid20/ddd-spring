package com.xyz.payment.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "payment_items")
@NoArgsConstructor
public class PaymentItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  Payment payment;

  public static PaymentItem of(BigDecimal amount) {
    final var paymentItem = new PaymentItem();
    paymentItem.setAmount(amount);
    return paymentItem;
  }
}
