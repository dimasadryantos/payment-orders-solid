package com.dimas.payments.application.validation;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.dimas.payments.support.Fixtures.DEBTOR;
import static com.dimas.payments.support.Fixtures.command;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentOrderValidatorTest {

    private final PaymentPolicy policy = new PaymentPolicy(
            Map.of(PaymentMethod.CARD, new BigDecimal("5000")),
            Map.of(PaymentMethod.SEPA_CREDIT_TRANSFER, Set.of(Currency.getInstance("EUR"))));

    private final PaymentOrderValidator validator = new PaymentOrderValidator(List.of(
            new PositiveAmountRule(),
            new DifferentAccountsRule(),
            new AllowedCurrencyRule(policy),
            new AmountLimitRule(policy)));

    @Test
    void acceptsAValidOrder() {
        assertDoesNotThrow(() -> validator.validate(command(PaymentMethod.CARD, "5000.00", "EUR")));
    }

    @Test
    void rejectsAnAmountAboveTheMethodLimit() {
        List<String> violations = violationsOf(command(PaymentMethod.CARD, "5000.01", "EUR"));

        assertEquals(List.of("5000.01 EUR is above the CARD limit of 5000"), violations);
    }

    @Test
    void rejectsACurrencyTheMethodDoesNotSupport() {
        List<String> violations = violationsOf(command(PaymentMethod.SEPA_CREDIT_TRANSFER, "10.00", "USD"));

        assertEquals(List.of("SEPA_CREDIT_TRANSFER does not support USD"), violations);
    }

    @Test
    void aMethodWithoutPolicyIsUnrestricted() {
        assertDoesNotThrow(() -> validator.validate(command(PaymentMethod.WALLET, "999999.00", "USD")));
    }

    @Test
    void reportsEveryViolationAtOnce() {
        CreatePaymentOrderCommand command = new CreatePaymentOrderCommand(
                DEBTOR, "de89 3704 0044 0532 0130 00", Money.of("0.00", "USD"),
                PaymentMethod.SEPA_CREDIT_TRANSFER, null);

        List<String> violations = violationsOf(command);

        assertEquals(3, violations.size());
        assertTrue(violations.contains("Amount must be greater than zero"));
        assertTrue(violations.contains("Debtor and creditor account must be different"));
        assertTrue(violations.contains("SEPA_CREDIT_TRANSFER does not support USD"));
    }

    @Test
    void aNewRuleNeedsNoChangeToTheValidator() {
        PaymentRule noCashReference = c -> "cash".equalsIgnoreCase(c.reference())
                ? Optional.of("Reference 'cash' is not allowed")
                : Optional.empty();
        PaymentOrderValidator extended = new PaymentOrderValidator(List.of(new PositiveAmountRule(), noCashReference));

        CreatePaymentOrderCommand command = new CreatePaymentOrderCommand(
                DEBTOR, "DE75512108001245126199", Money.of("10.00", "EUR"), PaymentMethod.CARD, "cash");

        PaymentRuleViolationException ex =
                assertThrows(PaymentRuleViolationException.class, () -> extended.validate(command));
        assertEquals(List.of("Reference 'cash' is not allowed"), ex.violations());
    }

    private List<String> violationsOf(CreatePaymentOrderCommand command) {
        return assertThrows(PaymentRuleViolationException.class, () -> validator.validate(command)).violations();
    }
}
