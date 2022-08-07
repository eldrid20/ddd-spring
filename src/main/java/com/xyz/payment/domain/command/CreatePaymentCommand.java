package com.xyz.payment.domain.command;

import lombok.Value;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class CreatePaymentCommand {
  private final BigDecimal amount;
}
