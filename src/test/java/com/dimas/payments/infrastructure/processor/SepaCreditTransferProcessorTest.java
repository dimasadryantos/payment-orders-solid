package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import org.junit.jupiter.api.Test;

import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SepaCreditTransferProcessorTest extends PaymentProcessorContractTest {

    private final SepaCreditTransferProcessor processor = new SepaCreditTransferProcessor();

    @Override
    protected PaymentProcessor processor() {
        return processor;
    }

    @Test
    void chargesAFlatFee() {
        PaymentResult.Approved approved = (PaymentResult.Approved)
                processor.process(order(PaymentMethod.SEPA_CREDIT_TRANSFER, "25000.00", "EUR"));

        assertEquals(Money.of("0.20", "EUR"), approved.fee());
    }
}
