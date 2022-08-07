package com.xyz.payment.domain.model;

import com.xyz.payment.domain.exception.InvalidPaymentStateException;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "payment")
@NoArgsConstructor
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Enumerated(EnumType.STRING)
  PaymentStatus status;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
  List<PaymentItem> items;

  public static Payment create(PaymentItem paymentItem) {
    var payment = new Payment();
    payment.setStatus(PaymentStatus.CREATED);
    payment.setItems(List.of(paymentItem));
    return payment;
  }

  public void complete() {
    validateStatus();
    this.status = PaymentStatus.COMPLETED;
  }

  public void addItem(PaymentItem orderItem) {
    items.add(orderItem);
  }

  public BigDecimal getTotalAmount() {
    return items.stream().map(PaymentItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void validateStatus() {
    if (status != PaymentStatus.CREATED) {
      throw new InvalidPaymentStateException("Order has invalid status:" + status);
    }
  }
}
