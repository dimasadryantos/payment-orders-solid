package com.dimas.payments.support;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class Fixtures {

    public static final String DEBTOR = "DE89370400440532013000";
    public static final String CREDITOR = "DE75512108001245126199";
    public static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    public static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private Fixtures() {
    }

    public static PaymentOrder order(PaymentMethod method, String amount, String currency) {
        return PaymentOrder.create(DEBTOR, CREDITOR, Money.of(amount, currency), method, "Invoice 2026-001", NOW);
    }

    public static CreatePaymentOrderCommand command(PaymentMethod method, String amount, String currency) {
        return new CreatePaymentOrderCommand(DEBTOR, CREDITOR, Money.of(amount, currency), method, "Invoice 2026-001");
    }
}
