package com.dimas.payments.api;

import com.dimas.payments.application.port.in.CancelPaymentOrderUseCase;
import com.dimas.payments.application.port.in.CreatePaymentOrderUseCase;
import com.dimas.payments.application.port.in.ExecutePaymentOrderUseCase;
import com.dimas.payments.application.port.in.GetPaymentOrderQuery;
import com.dimas.payments.application.port.in.RefundPaymentOrderUseCase;
import com.dimas.payments.domain.PaymentOrder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * HTTP in, HTTP out. It depends on one small interface per use case rather
 * than on a single fat "PaymentService" (Interface Segregation), and never on
 * a concrete service class (Dependency Inversion).
 */
@RestController
@RequestMapping("/api/v1/payment-orders")
public class PaymentOrderController {

    private final CreatePaymentOrderUseCase createPaymentOrder;
    private final ExecutePaymentOrderUseCase executePaymentOrder;
    private final CancelPaymentOrderUseCase cancelPaymentOrder;
    private final RefundPaymentOrderUseCase refundPaymentOrder;
    private final GetPaymentOrderQuery getPaymentOrder;

    public PaymentOrderController(CreatePaymentOrderUseCase createPaymentOrder,
                                  ExecutePaymentOrderUseCase executePaymentOrder,
                                  CancelPaymentOrderUseCase cancelPaymentOrder,
                                  RefundPaymentOrderUseCase refundPaymentOrder,
                                  GetPaymentOrderQuery getPaymentOrder) {
        this.createPaymentOrder = createPaymentOrder;
        this.executePaymentOrder = executePaymentOrder;
        this.cancelPaymentOrder = cancelPaymentOrder;
        this.refundPaymentOrder = refundPaymentOrder;
        this.getPaymentOrder = getPaymentOrder;
    }

    @PostMapping
    public ResponseEntity<PaymentOrderResponse> create(@Valid @RequestBody CreatePaymentOrderRequest request) {
        PaymentOrder order = createPaymentOrder.create(request.toCommand());
        URI location = URI.create("/api/v1/payment-orders/" + order.id());
        return ResponseEntity.created(location).body(PaymentOrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public PaymentOrderResponse get(@PathVariable UUID id) {
        return PaymentOrderResponse.from(getPaymentOrder.getById(id));
    }

    @PostMapping("/{id}/execute")
    public PaymentOrderResponse execute(@PathVariable UUID id) {
        return PaymentOrderResponse.from(executePaymentOrder.execute(id));
    }

    @PostMapping("/{id}/cancel")
    public PaymentOrderResponse cancel(@PathVariable UUID id) {
        return PaymentOrderResponse.from(cancelPaymentOrder.cancel(id));
    }

    @PostMapping("/{id}/refund")
    public PaymentOrderResponse refund(@PathVariable UUID id) {
        return PaymentOrderResponse.from(refundPaymentOrder.refund(id));
    }
}
