package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Runs every {@link PaymentRule} Spring knows about and reports all violations
 * at once, so the caller can fix them in one round trip.
 */
@Component
public class PaymentOrderValidator {

    private final List<PaymentRule> rules;

    public PaymentOrderValidator(List<PaymentRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public void validate(CreatePaymentOrderCommand command) {
        List<String> violations = rules.stream()
                .map(rule -> rule.violation(command))
                .flatMap(Optional::stream)
                .toList();
        if (!violations.isEmpty()) {
            throw new PaymentRuleViolationException(violations);
        }
    }
}
