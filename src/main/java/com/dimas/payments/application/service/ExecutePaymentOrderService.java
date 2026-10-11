package com.dimas.payments.application.service;

import com.dimas.payments.application.port.in.ExecutePaymentOrderUseCase;
import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;
import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Orchestrates one use case and nothing else: load, ask the right processor,
 * record the outcome. It does not know how a card or a SEPA transfer works
 * (Single Responsibility), and it only talks to interfaces (Dependency Inversion).
 */
@Service
public class ExecutePaymentOrderService implements ExecutePaymentOrderUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentProcessorRegistry processors;
    private final PaymentEventPublisher events;
    private final Clock clock;

    public ExecutePaymentOrderService(PaymentOrderRepository repository, PaymentProcessorRegistry processors,
                                      PaymentEventPublisher events, Clock clock) {
        this.repository = repository;
        this.processors = processors;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public PaymentOrder execute(UUID orderId) {
        PaymentOrder order = repository.findById(orderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(orderId));
        order.ensureExecutable();

        PaymentResult result = processors.forMethod(order.method()).process(order);

        Instant now = clock.instant();
        switch (result) {
            case PaymentResult.Approved approved -> order.settle(approved.providerReference(), approved.fee(), now);
            case PaymentResult.Declined declined -> order.fail(declined.reason(), now);
        }
        repository.save(order);
        events.publish(PaymentEvent.of(order));
        return order;
    }
}
