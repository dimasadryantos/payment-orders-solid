package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DifferentAccountsRule implements PaymentRule {

    @Override
    public Optional<String> violation(CreatePaymentOrderCommand command) {
        return normalize(command.debtorAccount()).equals(normalize(command.creditorAccount()))
                ? Optional.of("Debtor and creditor account must be different")
                : Optional.empty();
    }

    private static String normalize(String account) {
        return account.replace(" ", "").toUpperCase();
    }
}
