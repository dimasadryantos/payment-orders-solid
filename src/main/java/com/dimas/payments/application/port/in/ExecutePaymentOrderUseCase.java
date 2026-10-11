package com.dimas.payments.application.port.in;

import com.dimas.payments.domain.PaymentOrder;

import java.util.UUID;

public interface ExecutePaymentOrderUseCase {

    PaymentOrder execute(UUID orderId);
}
