package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import org.junit.jupiter.api.Test;

import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletPaymentProcessorTest extends PaymentProcessorContractTest {

    private final WalletPaymentProcessor processor = new WalletPaymentProcessor();

    @Override
    protected PaymentProcessor processor() {
        return processor;
    }

    @Test
    void isFreeWithinTheBalance() {
        PaymentResult.Approved approved = (PaymentResult.Approved)
                processor.process(order(PaymentMethod.WALLET, "1000.00", "EUR"));

        assertEquals(Money.of("0.00", "EUR"), approved.fee());
    }

    @Test
    void declinesAboveTheBalanceInsteadOfThrowing() {
        PaymentResult result = processor.process(order(PaymentMethod.WALLET, "1000.01", "EUR"));

        assertEquals(new PaymentResult.Declined("Insufficient wallet balance"), result);
    }
}
