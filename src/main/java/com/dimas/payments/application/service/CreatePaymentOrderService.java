package com.dimas.payments.application.service;

import com.dimas.payments.application.port.in.CreatePaymentOrderCommand;
import com.dimas.payments.application.port.in.CreatePaymentOrderUseCase;
import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;
import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.application.validation.PaymentOrderValidator;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class CreatePaymentOrderService implements CreatePaymentOrderUseCase {

    private final PaymentOrderValidator validator;
    private final PaymentOrderRepository repository;
    private final PaymentEventPublisher events;
    private final Clock clock;

    public CreatePaymentOrderService(PaymentOrderValidator validator, PaymentOrderRepository repository,
                                     PaymentEventPublisher events, Clock clock) {
        this.validator = validator;
        this.repository = repository;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public PaymentOrder create(CreatePaymentOrderCommand command) {
        validator.validate(command);
        PaymentOrder order = PaymentOrder.create(command.debtorAccount(), command.creditorAccount(),
                command.amount(), command.method(), command.reference(), clock.instant());
        repository.save(order);
        events.publish(PaymentEvent.of(order));
        return order;
    }
}
