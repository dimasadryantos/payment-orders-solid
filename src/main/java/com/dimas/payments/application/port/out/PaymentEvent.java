package com.dimas.payments.application.port.out;

import com.dimas.payments.domain.PaymentOrder;
import com.dimas.payments.domain.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentEvent(UUID orderId, PaymentStatus status, Instant occurredAt) {

    public static PaymentEvent of(PaymentOrder order) {
        return new PaymentEvent(order.id(), order.status(), order.updatedAt());
    }
}
