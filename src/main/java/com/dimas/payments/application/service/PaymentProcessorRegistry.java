package com.dimas.payments.application.service;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.RefundablePaymentProcessor;
import com.dimas.payments.domain.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds the processor for a payment method. Spring hands in every
 * {@link PaymentProcessor} bean, so there is no switch or if/else chain here to
 * extend when a method is added (Open/Closed).
 */
@Component
public class PaymentProcessorRegistry {

    private final Map<PaymentMethod, PaymentProcessor> processors;
    private final Map<PaymentMethod, RefundablePaymentProcessor> refundable;

    public PaymentProcessorRegistry(List<PaymentProcessor> processors,
                                    List<RefundablePaymentProcessor> refundable) {
        this.processors = index(processors);
        this.refundable = index(refundable);
    }

    public PaymentProcessor forMethod(PaymentMethod method) {
        PaymentProcessor processor = processors.get(method);
        if (processor == null) {
            throw new IllegalStateException("No payment processor is registered for " + method);
        }
        return processor;
    }

    public Optional<RefundablePaymentProcessor> refundableFor(PaymentMethod method) {
        return Optional.ofNullable(refundable.get(method));
    }

    private static <P extends PaymentProcessor> Map<PaymentMethod, P> index(List<P> candidates) {
        Map<PaymentMethod, P> byMethod = new EnumMap<>(PaymentMethod.class);
        for (P candidate : candidates) {
            P existing = byMethod.putIfAbsent(candidate.method(), candidate);
            if (existing != null) {
                throw new IllegalStateException("Two payment processors are registered for %s: %s and %s"
                        .formatted(candidate.method(), existing.getClass().getSimpleName(),
                                candidate.getClass().getSimpleName()));
            }
        }
        return byMethod;
    }
}
