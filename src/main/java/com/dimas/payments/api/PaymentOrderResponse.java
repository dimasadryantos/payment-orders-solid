package com.dimas.payments.api;

import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import com.dimas.payments.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentOrderResponse(
        UUID id,
        PaymentStatus status,
        PaymentMethod method,
        String debtorAccount,
        String creditorAccount,
        BigDecimal amount,
        String currency,
        BigDecimal fee,
        String reference,
        String providerReference,
        String refundReference,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    static PaymentOrderResponse from(PaymentOrder order) {
        return new PaymentOrderResponse(
                order.id(),
                order.status(),
                order.method(),
                order.debtorAccount(),
                order.creditorAccount(),
                order.amount().amount(),
                order.amount().currency().getCurrencyCode(),
                order.fee() == null ? null : order.fee().amount(),
                order.reference(),
                order.providerReference(),
                order.refundReference(),
                order.failureReason(),
                order.createdAt(),
                order.updatedAt());
    }
}
