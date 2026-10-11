package com.dimas.payments.application.port.out;

public interface PaymentEventPublisher {

    void publish(PaymentEvent event);
}
