package com.dimas.payments.application.service;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.PaymentResult;
import com.dimas.payments.domain.IllegalPaymentStateException;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import com.dimas.payments.domain.PaymentStatus;
import com.dimas.payments.infrastructure.persistence.InMemoryPaymentOrderRepository;
import com.dimas.payments.support.RecordingEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.dimas.payments.support.Fixtures.CLOCK;
import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutePaymentOrderServiceTest {

    private InMemoryPaymentOrderRepository repository;
    private RecordingEventPublisher events;
    private CountingProcessor card;
    private ExecutePaymentOrderService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentOrderRepository();
        events = new RecordingEventPublisher();
        card = new CountingProcessor(PaymentMethod.CARD,
                new PaymentResult.Approved("CARD-1", Money.of("1.65", "EUR")));
        PaymentProcessor wallet = new CountingProcessor(PaymentMethod.WALLET,
                new PaymentResult.Declined("Insufficient wallet balance"));
        service = new ExecutePaymentOrderService(repository,
                new PaymentProcessorRegistry(List.of(card, wallet), List.of()), events, CLOCK);
    }

    @Test
    void settlesAnApprovedPaymentAndPublishesAnEvent() {
        PaymentOrder order = repository.save(order(PaymentMethod.CARD, "100.00", "EUR"));

        PaymentOrder result = service.execute(order.id());

        assertEquals(PaymentStatus.SETTLED, result.status());
        assertEquals("CARD-1", result.providerReference());
        assertEquals(Money.of("1.65", "EUR"), result.fee());
        assertEquals(1, events.events().size());
        assertEquals(PaymentStatus.SETTLED, events.events().get(0).status());
    }

    @Test
    void marksADeclinedPaymentAsFailed() {
        PaymentOrder order = repository.save(order(PaymentMethod.WALLET, "1500.00", "EUR"));

        PaymentOrder result = service.execute(order.id());

        assertEquals(PaymentStatus.FAILED, result.status());
        assertEquals("Insufficient wallet balance", result.failureReason());
    }

    @Test
    void neverChargesTheSameOrderTwice() {
        PaymentOrder order = repository.save(order(PaymentMethod.CARD, "100.00", "EUR"));
        service.execute(order.id());

        assertThrows(IllegalPaymentStateException.class, () -> service.execute(order.id()));
        assertEquals(1, card.calls);
    }

    @Test
    void failsForAnUnknownOrder() {
        assertThrows(PaymentOrderNotFoundException.class, () -> service.execute(UUID.randomUUID()));
    }

    /** Any PaymentProcessor can be dropped in: the service cannot tell a fake from the real one. */
    private static final class CountingProcessor implements PaymentProcessor {

        private final PaymentMethod method;
        private final PaymentResult answer;
        private int calls;

        private CountingProcessor(PaymentMethod method, PaymentResult answer) {
            this.method = method;
            this.answer = answer;
        }

        @Override
        public PaymentMethod method() {
            return method;
        }

        @Override
        public PaymentResult process(PaymentOrder order) {
            calls++;
            return answer;
        }
    }
}
