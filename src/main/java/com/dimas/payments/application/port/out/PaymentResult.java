package com.dimas.payments.application.port.out;

import com.dimas.payments.domain.Money;

/**
 * What a provider answered. A decline is a normal business outcome, so it is a
 * value, not an exception.
 */
public sealed interface PaymentResult {

    record Approved(String providerReference, Money fee) implements PaymentResult {
    }

    record Declined(String reason) implements PaymentResult {
    }
}
