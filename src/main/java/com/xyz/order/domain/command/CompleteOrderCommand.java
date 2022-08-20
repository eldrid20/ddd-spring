package com.xyz.order.domain.command;

import lombok.Value;

@Value(staticConstructor = "of")
public class CompleteOrderCommand {

  private Long orderId;
}
