package com.dimas.payments.domain;

import java.util.UUID;

public class IllegalPaymentStateException extends RuntimeException {

    public IllegalPaymentStateException(UUID orderId, String action, PaymentStatus current, PaymentStatus required) {
        super("Cannot %s payment order %s: it is %s, but must be %s".formatted(action, orderId, current, required));
    }
}
