package com.dimas.payments.domain;

/**
 * CREATED -> SETTLED | FAILED | CANCELLED, and SETTLED -> REFUNDED.
 */
public enum PaymentStatus {
    CREATED,
    SETTLED,
    FAILED,
    CANCELLED,
    REFUNDED
}
