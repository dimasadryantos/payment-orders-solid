package com.dimas.payments.application.port.in;

import com.dimas.payments.domain.PaymentOrder;

public interface CreatePaymentOrderUseCase {

    PaymentOrder create(CreatePaymentOrderCommand command);
}
