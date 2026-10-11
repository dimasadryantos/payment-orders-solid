package com.dimas.payments.infrastructure.persistence;

import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps orders in memory so the project runs with no database. Swapping in
 * JPA means writing one more class that implements the same port.
 */
@Repository
public class InMemoryPaymentOrderRepository implements PaymentOrderRepository {

    private final Map<UUID, PaymentOrder> orders = new ConcurrentHashMap<>();

    @Override
    public PaymentOrder save(PaymentOrder order) {
        orders.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<PaymentOrder> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }
}
