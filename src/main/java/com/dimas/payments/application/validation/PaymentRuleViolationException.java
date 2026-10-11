package com.dimas.payments.application.validation;

import java.util.List;

public class PaymentRuleViolationException extends RuntimeException {

    private final List<String> violations;

    public PaymentRuleViolationException(List<String> violations) {
        super("Payment order rejected: " + String.join("; ", violations));
        this.violations = List.copyOf(violations);
    }

    public List<String> violations() {
        return violations;
    }
}
