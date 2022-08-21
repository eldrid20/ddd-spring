package com.xyz.order.domain.command;

import lombok.Value;

@Value(staticConstructor = "of")
public class CompleteOrder {

  private Long orderId;
}
