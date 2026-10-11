package com.dimas.payments.application.service;

import java.util.UUID;

public class PaymentOrderNotFoundException extends RuntimeException {

    public PaymentOrderNotFoundException(UUID orderId) {
        super("Payment order " + orderId + " does not exist");
    }
}
