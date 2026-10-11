package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;

import java.util.Optional;

/**
 * One business rule a new payment order must pass. A new rule is a new
 * {@code @Component}; the validator picks it up without being edited (Open/Closed).
 */
public interface PaymentRule {

    /** @return a message describing the violation, or empty if the command passes */
    Optional<String> violation(CreatePaymentOrderCommand command);
}
