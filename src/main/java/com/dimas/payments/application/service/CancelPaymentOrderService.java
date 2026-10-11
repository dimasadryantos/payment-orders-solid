package com.dimas.payments.application.service;

import com.dimas.payments.application.port.in.CancelPaymentOrderUseCase;
import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;
import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
public class CancelPaymentOrderService implements CancelPaymentOrderUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentEventPublisher events;
    private final Clock clock;

    public CancelPaymentOrderService(PaymentOrderRepository repository, PaymentEventPublisher events, Clock clock) {
        this.repository = repository;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public PaymentOrder cancel(UUID orderId) {
        PaymentOrder order = repository.findById(orderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(orderId));
        order.cancel(clock.instant());
        repository.save(order);
        events.publish(PaymentEvent.of(order));
        return order;
    }
}
