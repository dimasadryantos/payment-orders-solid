package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class AmountLimitRule implements PaymentRule {

    private final PaymentPolicy policy;

    public AmountLimitRule(PaymentPolicy policy) {
        this.policy = policy;
    }

    @Override
    public Optional<String> violation(CreatePaymentOrderCommand command) {
        BigDecimal max = policy.maxAmount().get(command.method());
        if (max == null || !command.amount().isGreaterThan(max)) {
            return Optional.empty();
        }
        return Optional.of("%s is above the %s limit of %s"
                .formatted(command.amount(), command.method(), max.toPlainString()));
    }
}
