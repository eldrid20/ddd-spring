package com.xyz.order.domain.model;

import com.xyz.order.domain.exception.InvalidStateException;
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
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
  List<OrderItem> items;

  public static Order create(OrderItem orderItem) {
    var order = new Order();
    order.setStatus(OrderStatus.CREATED);

    List<OrderItem> items = new ArrayList<>();
    items.add(orderItem);
    order.setItems(items);
    return order;
  }

  public void complete() {
    validateStatus();
    this.status = OrderStatus.COMPLETED;
  }

  public void addItem(OrderItem orderItem) {
    validateStatus();
    items.add(orderItem);
  }

  public BigDecimal getTotalAmount() {
    return items.stream().map(OrderItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void validateStatus() {
    if (status != OrderStatus.CREATED) {
      throw new InvalidStateException("Order has invalid status:" + status);
    }
  }
}
