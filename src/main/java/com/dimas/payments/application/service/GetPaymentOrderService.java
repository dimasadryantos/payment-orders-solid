package com.dimas.payments.application.service;

import com.dimas.payments.application.port.in.GetPaymentOrderQuery;
import com.dimas.payments.application.port.out.PaymentOrderRepository;
import com.dimas.payments.domain.PaymentOrder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPaymentOrderService implements GetPaymentOrderQuery {

    private final PaymentOrderRepository repository;

    public GetPaymentOrderService(PaymentOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentOrder getById(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException(orderId));
    }
}
