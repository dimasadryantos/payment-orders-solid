package com.dimas.payments.application.service;

import com.dimas.payments.domain.PaymentMethod;

public class RefundNotSupportedException extends RuntimeException {

    public RefundNotSupportedException(PaymentMethod method) {
        super(method + " payments cannot be refunded");
    }
}
