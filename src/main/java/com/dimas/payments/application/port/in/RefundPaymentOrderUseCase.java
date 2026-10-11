package com.dimas.payments.application.port.in;

import com.dimas.payments.domain.PaymentOrder;

import java.util.UUID;

public interface RefundPaymentOrderUseCase {

    PaymentOrder refund(UUID orderId);
}
