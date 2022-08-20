package com.xyz.order.adapter.incoming.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItemDto {
  @NotNull private BigDecimal amount;
}
