package com.dimas.payments.application.port.in;

import com.dimas.payments.domain.PaymentOrder;

import java.util.UUID;

public interface CancelPaymentOrderUseCase {

    PaymentOrder cancel(UUID orderId);
}
