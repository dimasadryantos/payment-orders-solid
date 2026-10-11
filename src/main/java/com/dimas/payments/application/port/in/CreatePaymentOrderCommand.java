package com.dimas.payments.application.port.in;

import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;

public record CreatePaymentOrderCommand(
        String debtorAccount,
        String creditorAccount,
        Money amount,
        PaymentMethod method,
        String reference) {
}
