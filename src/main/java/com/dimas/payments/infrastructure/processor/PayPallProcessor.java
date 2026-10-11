package com.dimas.payments.infrastructure.processor;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Component;

@Component
public class PayPallProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.PAYPAL;
    }

    @Override
    public PaymentResult process(PaymentOrder order) {
        return null;
    }
}
