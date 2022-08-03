package com.xyz.payment.adapter.incoming.rest;

import com.xyz.payment.adapter.incoming.rest.dto.PaymentDto;
import com.xyz.payment.application.PaymentProcessor;
import com.xyz.payment.domain.command.AddPaymentItemCommand;
import com.xyz.payment.domain.command.CompletePaymentCommand;
import com.xyz.payment.domain.command.CreatePaymentCommand;
import com.xyz.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentProcessor processor;

    @PostMapping(value = "/v1/payments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Payment createPayment(@RequestBody @Valid PaymentDto paymentDto){
        return processor.createPayment(CreatePaymentCommand.of(paymentDto.getAmount()));
    }

    @PutMapping(value = "/v1/payments/{paymentId}/items")
    public Payment addPaymentItem(@PathVariable Long paymentId, @RequestBody @Valid PaymentDto paymentDto){
        return processor.addPaymentItem(AddPaymentItemCommand.of(paymentId, paymentDto.getAmount()));
    }

    @PutMapping(value = "/payments/{paymentId}/complete")
    public Payment completePaymentItem(@PathVariable Long paymentId){
        return processor.completePayment(CompletePaymentCommand.of(paymentId));
    }
}
