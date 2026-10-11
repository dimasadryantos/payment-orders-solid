package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Simulated SEPA credit transfer: flat fee, always accepted. A settled credit
 * transfer is final, so this processor deliberately is not refundable.
 */
@Component
public class SepaCreditTransferProcessor implements PaymentProcessor {

    private static final BigDecimal FLAT_FEE = new BigDecimal("0.20");

    @Override
    public PaymentMethod method() {
        return PaymentMethod.SEPA_CREDIT_TRANSFER;
    }

    @Override
    public PaymentResult process(PaymentOrder order) {
        var currency = order.amount().currency();
        Money fee = new Money(FLAT_FEE.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN), currency);
        return new PaymentResult.Approved("SEPA-" + UUID.randomUUID(), fee);
    }
}
