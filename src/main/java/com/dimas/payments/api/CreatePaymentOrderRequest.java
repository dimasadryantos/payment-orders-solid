package com.dimas.payments.api;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentOrderRequest(
        @NotBlank String debtorAccount,
        @NotBlank String creditorAccount,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotNull PaymentMethod method,
        @Size(max = 140) String reference) {

    CreatePaymentOrderCommand toCommand() {
        return new CreatePaymentOrderCommand(debtorAccount, creditorAccount,
                Money.of(amount, currency), method, reference);
    }
}
