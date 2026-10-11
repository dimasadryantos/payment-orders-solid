package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Optional;
import java.util.Set;

@Component
public class AllowedCurrencyRule implements PaymentRule {

    private final PaymentPolicy policy;

    public AllowedCurrencyRule(PaymentPolicy policy) {
        this.policy = policy;
    }

    @Override
    public Optional<String> violation(CreatePaymentOrderCommand command) {
        Set<Currency> allowed = policy.allowedCurrencies().get(command.method());
        Currency currency = command.amount().currency();
        if (allowed == null || allowed.contains(currency)) {
            return Optional.empty();
        }
        return Optional.of("%s does not support %s".formatted(command.method(), currency));
    }
}
