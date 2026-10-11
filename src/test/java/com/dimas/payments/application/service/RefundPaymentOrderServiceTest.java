package com.dimas.payments.application.service;

import com.dimas.payments.application.port.out.PaymentProcessor;
import com.dimas.payments.application.port.out.RefundablePaymentProcessor;
import com.dimas.payments.domain.IllegalPaymentStateException;
import com.dimas.payments.domain.Money;
import com.dimas.payments.domain.PaymentMethod;
import com.dimas.payments.domain.PaymentOrder;
import com.dimas.payments.domain.PaymentStatus;
import com.dimas.payments.infrastructure.persistence.InMemoryPaymentOrderRepository;
import com.dimas.payments.infrastructure.processor.CardPaymentProcessor;
import com.dimas.payments.infrastructure.processor.SepaCreditTransferProcessor;
import com.dimas.payments.infrastructure.processor.WalletPaymentProcessor;
import com.dimas.payments.support.RecordingEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.dimas.payments.support.Fixtures.CLOCK;
import static com.dimas.payments.support.Fixtures.NOW;
import static com.dimas.payments.support.Fixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefundPaymentOrderServiceTest {

    private InMemoryPaymentOrderRepository repository;
    private RefundPaymentOrderService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentOrderRepository();
        CardPaymentProcessor card = new CardPaymentProcessor();
        WalletPaymentProcessor wallet = new WalletPaymentProcessor();
        List<PaymentProcessor> all = List.of(new SepaCreditTransferProcessor(), card, wallet);
        List<RefundablePaymentProcessor> refundable = List.of(card, wallet);
        service = new RefundPaymentOrderService(repository,
                new PaymentProcessorRegistry(all, refundable), new RecordingEventPublisher(), CLOCK);
    }

    @Test
    void refundsASettledCardPayment() {
        PaymentOrder order = settled(PaymentMethod.CARD);

        PaymentOrder result = service.refund(order.id());

        assertEquals(PaymentStatus.REFUNDED, result.status());
        assertTrue(result.refundReference().startsWith("CARD-REFUND-"));
    }

    @Test
    void sepaCreditTransfersCannotBeRefunded() {
        PaymentOrder order = settled(PaymentMethod.SEPA_CREDIT_TRANSFER);

        assertThrows(RefundNotSupportedException.class, () -> service.refund(order.id()));
        assertEquals(PaymentStatus.SETTLED, order.status());
    }

    @Test
    void onlySettledOrdersCanBeRefunded() {
        PaymentOrder order = repository.save(order(PaymentMethod.CARD, "100.00", "EUR"));

        assertThrows(IllegalPaymentStateException.class, () -> service.refund(order.id()));
    }

    private PaymentOrder settled(PaymentMethod method) {
        PaymentOrder order = order(method, "100.00", "EUR");
        order.settle("PROVIDER-1", Money.of("0.20", "EUR"), NOW);
        return repository.save(order);
    }
}
