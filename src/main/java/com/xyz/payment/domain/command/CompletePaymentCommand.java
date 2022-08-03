package com.xyz.payment.domain.command;

import lombok.Value;

@Value(staticConstructor = "of")
public class CompletePaymentCommand {

    private Long paymentId;
}
