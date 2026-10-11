package com.dimas.payments.application.port.out;

import com.dimas.payments.domain.PaymentOrder;

import java.util.Optional;
import java.util.UUID;

/**
 * Owned by the application layer, implemented by infrastructure. The services
 * depend on this abstraction, not on a database (Dependency Inversion).
 */
public interface PaymentOrderRepository {

    PaymentOrder save(PaymentOrder order);

    Optional<PaymentOrder> findById(UUID id);
}
