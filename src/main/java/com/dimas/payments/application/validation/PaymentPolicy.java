package com.dimas.payments.application.validation;

import com.dimas.payments.domain.PaymentMethod;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

/**
 * Limits and allowed currencies per payment method, bound from
 * {@code payments.policy} in application.yml. A method with no entry is
 * unrestricted.
 */
@ConfigurationProperties(prefix = "payments.policy")
public record PaymentPolicy(
        Map<PaymentMethod, BigDecimal> maxAmount,
        Map<PaymentMethod, Set<Currency>> allowedCurrencies) {

    public PaymentPolicy {
        maxAmount = maxAmount == null ? Map.of() : Map.copyOf(maxAmount);
        allowedCurrencies = allowedCurrencies == null ? Map.of() : Map.copyOf(allowedCurrencies);
    }
}
