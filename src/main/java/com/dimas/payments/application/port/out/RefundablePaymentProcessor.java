package com.dimas.payments.application.port.out;

import com.dimas.payments.domain.PaymentOrder;

/**
 * Only the methods that can actually send money back implement this. A SEPA
 * credit transfer is final once settled, so its processor is not forced to
 * carry a refund() that could only throw (Interface Segregation).
 */
public interface RefundablePaymentProcessor extends PaymentProcessor {

    /** @return the provider's reference for the refund */
    String refund(PaymentOrder order);
}
