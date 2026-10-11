package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import org.junit.jupiter.api.Test;

import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardPaymentProcessorTest extends PaymentProcessorContractTest {

    private final CardPaymentProcessor processor = new CardPaymentProcessor();

    @Override
    protected PaymentProcessor processor() {
        return processor;
    }

    @Test
    void chargesAPercentagePlusAFixedFee() {
        PaymentResult.Approved approved = (PaymentResult.Approved)
                processor.process(order(PaymentMethod.CARD, "100.00", "EUR"));

        assertEquals(Money.of("1.65", "EUR"), approved.fee());
    }
}
