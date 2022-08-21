package com.xyz.order.domain.command;

import lombok.Value;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class CreateOrder {
  private final BigDecimal amount;
}
