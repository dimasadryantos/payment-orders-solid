package com.dimas.payments.application.service;

import com.dimas.payments.application.port.in.RefundPaymentOrderUseCase;
import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;
import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.application.port.out.RefundablePaymentProcessor;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
public class RefundPaymentOrderService implements RefundPaymentOrderUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentProcessorRegistry processors;
    private final PaymentEventPublisher events;
    private final Clock clock;

    public RefundPaymentOrderService(PaymentOrderRepository repository, PaymentProcessorRegistry processors,
                                     PaymentEventPublisher events, Clock clock) {
        this.repository = repository;
        this.processors = processors;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public PaymentOrder refund(UUID orderId) {
        PaymentOrder order = repository.findById(orderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(orderId));
        order.ensureRefundable();
        RefundablePaymentProcessor processor = processors.refundableFor(order.method())
                .orElseThrow(() -> new RefundNotSupportedException(order.method()));

        String refundReference = processor.refund(order);

        order.refund(refundReference, clock.instant());
        repository.save(order);
        events.publish(PaymentEvent.of(order));
        return order;
    }
}
