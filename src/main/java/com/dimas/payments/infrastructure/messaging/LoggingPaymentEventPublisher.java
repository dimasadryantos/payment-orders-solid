package com.dimas.payments.infrastructure.messaging;

import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Stands in for Kafka: the services only know the PaymentEventPublisher port. */
@Component
public class LoggingPaymentEventPublisher implements PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingPaymentEventPublisher.class);

    @Override
    public void publish(PaymentEvent event) {
        log.info("Payment order {} is now {} (at {})", event.orderId(), event.status(), event.occurredAt());
    }
}
