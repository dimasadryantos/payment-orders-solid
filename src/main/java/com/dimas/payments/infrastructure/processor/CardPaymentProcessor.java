package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.application.port.out.RefundablePaymentProcessor;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/** Simulated card acquirer: 1.4 % + 0.25 per transaction, refundable. */
@Component
public class CardPaymentProcessor implements RefundablePaymentProcessor {

    private static final BigDecimal PERCENT_FEE = new BigDecimal("1.4");
    private static final BigDecimal FIXED_FEE = new BigDecimal("0.25");

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CARD;
    }

    @Override
    public PaymentResult process(PaymentOrder order) {
        Money amount = order.amount();
        var currency = amount.currency();
        Money fixed = new Money(FIXED_FEE.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN), currency);
        Money fee = amount.percentage(PERCENT_FEE).plus(fixed);
        return new PaymentResult.Approved("CARD-" + UUID.randomUUID(), fee);
    }

    @Override
    public String refund(PaymentOrder order) {
        return "CARD-REFUND-" + UUID.randomUUID();
    }
}
