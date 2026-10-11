package com.dimas.payments.application.port.out;

import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;

/**
 * One implementation per payment method. Adding a method means adding a class
 * that implements this interface; no existing class is edited (Open/Closed).
 *
 * <p>The contract every implementation must keep, so that any of them can
 * stand in for another (Liskov Substitution):
 * <ul>
 *   <li>{@link #process} never mutates the order.</li>
 *   <li>A business decline is returned as {@link PaymentResult.Declined}, never thrown.</li>
 *   <li>An approval carries a non-blank provider reference and a fee in the order's currency.</li>
 * </ul>
 * {@code PaymentProcessorContractTest} runs these checks against every implementation.
 */
public interface PaymentProcessor {

    PaymentMethod method();

    PaymentResult process(PaymentOrder order);
}
