package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.application.port.out.RefundablePaymentProcessor;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulated wallet: free, refundable, and every wallet holds 1,000. Anything
 * above that is declined, which is how you see the FAILED path in the demo.
 */
@Component
public class WalletPaymentProcessor implements RefundablePaymentProcessor {

    private static final BigDecimal SIMULATED_BALANCE = new BigDecimal("1000");

    @Override
    public PaymentMethod method() {
        return PaymentMethod.WALLET;
    }

    @Override
    public PaymentResult process(PaymentOrder order) {
        Money amount = order.amount();
        if (amount.isGreaterThan(SIMULATED_BALANCE)) {
            return new PaymentResult.Declined("Insufficient wallet balance");
        }
        return new PaymentResult.Approved("WALLET-" + UUID.randomUUID(), Money.zero(amount.currency()));
    }

    @Override
    public String refund(PaymentOrder order) {
        return "WALLET-REFUND-" + UUID.randomUUID();
    }
}
