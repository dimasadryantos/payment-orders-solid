package com.dimas.payments.support;

import com.dimas.payments.application.port.out.PaymentEvent;
import com.dimas.payments.application.port.out.PaymentEventPublisher;

import java.util.ArrayList;
import java.util.List;

/** A hand-written fake. Possible only because the services depend on the port, not on Kafka. */
public class RecordingEventPublisher implements PaymentEventPublisher {

    private final List<PaymentEvent> events = new ArrayList<>();

    @Override
    public void publish(PaymentEvent event) {
        events.add(event);
    }

    public List<PaymentEvent> events() {
        return events;
    }
}
