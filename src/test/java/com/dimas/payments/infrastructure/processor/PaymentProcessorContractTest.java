package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.PaymentOrder;
import com.dimas.payments.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Liskov Substitution as a test. Every processor's test class extends this, so
 * each implementation has to pass the same checks. If a new processor throws
 * on a decline or changes the order, it fails here before it reaches the service.
 */
abstract class PaymentProcessorContractTest {

    protected abstract PaymentProcessor processor();

    @Test
    void declaresTheMethodItHandles() {
        assertNotNull(processor().method());
    }

    @Test
    void anApprovalCarriesAReferenceAndAFeeInTheOrderCurrency() {
        PaymentOrder order = order(processor().method(), "100.00", "EUR");

        PaymentResult result = processor().process(order);

        PaymentResult.Approved approved = (PaymentResult.Approved) result;
        assertFalse(approved.providerReference().isBlank());
        assertEquals(order.amount().currency(), approved.fee().currency());
    }

    @Test
    void neverChangesTheOrderItIsGiven() {
        PaymentOrder order = order(processor().method(), "100.00", "EUR");

        processor().process(order);

        assertEquals(PaymentStatus.CREATED, order.status());
        assertNull(order.providerReference());
        assertNull(order.fee());
    }

    @Test
    void answersEvenForAnAmountItWillNotAccept() {
        PaymentOrder order = order(processor().method(), "999999.00", "EUR");

        PaymentResult result = processor().process(order);

        assertNotNull(result);
        if (result instanceof PaymentResult.Declined declined) {
            assertFalse(declined.reason().isBlank());
        }
    }
}
