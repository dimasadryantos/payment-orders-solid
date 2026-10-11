package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PositiveAmountRule implements PaymentRule {

    @Override
    public Optional<String> violation(CreatePaymentOrderCommand command) {
        return command.amount().isZero()
                ? Optional.of("Amount must be greater than zero")
                : Optional.empty();
    }
}
