package com.dimas.payments.domain;

import org.junit.jupiter.api.Test;

import static com.dimas.payments.support.Fixtures.NOW;
import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentOrderTest {

    @Test
    void startsAsCreated() {
        assertEquals(PaymentStatus.CREATED, order(PaymentMethod.CARD, "100.00", "EUR").status());
    }

    @Test
    void settlesWithProviderReferenceAndFee() {
        PaymentOrder order = order(PaymentMethod.CARD, "100.00", "EUR");

        order.settle("CARD-1", Money.of("1.65", "EUR"), NOW.plusSeconds(5));

        assertEquals(PaymentStatus.SETTLED, order.status());
        assertEquals("CARD-1", order.providerReference());
        assertEquals(Money.of("1.65", "EUR"), order.fee());
        assertEquals(NOW.plusSeconds(5), order.updatedAt());
    }

    @Test
    void cannotBeSettledTwice() {
        PaymentOrder order = order(PaymentMethod.CARD, "100.00", "EUR");
        order.settle("CARD-1", Money.of("1.65", "EUR"), NOW);

        assertThrows(IllegalPaymentStateException.class, order::ensureExecutable);
        assertThrows(IllegalPaymentStateException.class,
                () -> order.settle("CARD-2", Money.of("1.65", "EUR"), NOW));
    }

    @Test
    void canOnlyBeCancelledBeforeExecution() {
        PaymentOrder settled = order(PaymentMethod.CARD, "100.00", "EUR");
        settled.settle("CARD-1", Money.of("1.65", "EUR"), NOW);
        assertThrows(IllegalPaymentStateException.class, () -> settled.cancel(NOW));

        PaymentOrder fresh = order(PaymentMethod.CARD, "100.00", "EUR");
        fresh.cancel(NOW);
        assertEquals(PaymentStatus.CANCELLED, fresh.status());
    }

    @Test
    void canOnlyBeRefundedOnceSettled() {
        PaymentOrder order = order(PaymentMethod.CARD, "100.00", "EUR");
        assertThrows(IllegalPaymentStateException.class, order::ensureRefundable);
        assertThrows(IllegalPaymentStateException.class, () -> order.refund("REFUND-1", NOW));

        order.settle("CARD-1", Money.of("1.65", "EUR"), NOW);
        order.refund("REFUND-1", NOW);

        assertEquals(PaymentStatus.REFUNDED, order.status());
        assertEquals("REFUND-1", order.refundReference());
    }

    @Test
    void aFailedOrderKeepsTheReason() {
        PaymentOrder order = order(PaymentMethod.WALLET, "1500.00", "EUR");

        order.fail("Insufficient wallet balance", NOW);

        assertEquals(PaymentStatus.FAILED, order.status());
        assertEquals("Insufficient wallet balance", order.failureReason());
    }
}
