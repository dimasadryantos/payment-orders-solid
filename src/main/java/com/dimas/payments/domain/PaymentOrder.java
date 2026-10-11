package com.dimas.payments.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The payment order and its lifecycle. This class has exactly one reason to
 * change: the rules about which state an order may move to. It knows nothing
 * about HTTP, storage or payment providers.
 */
public final class PaymentOrder {

    private final UUID id;
    private final String debtorAccount;
    private final String creditorAccount;
    private final Money amount;
    private final PaymentMethod method;
    private final String reference;
    private final Instant createdAt;

    private PaymentStatus status;
    private Money fee;
    private String providerReference;
    private String refundReference;
    private String failureReason;
    private Instant updatedAt;

    private PaymentOrder(UUID id, String debtorAccount, String creditorAccount, Money amount,
                         PaymentMethod method, String reference, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "debtorAccount");
        this.creditorAccount = Objects.requireNonNull(creditorAccount, "creditorAccount");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.method = Objects.requireNonNull(method, "method");
        this.reference = reference;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = createdAt;
        this.status = PaymentStatus.CREATED;
    }

    public static PaymentOrder create(String debtorAccount, String creditorAccount, Money amount,
                                      PaymentMethod method, String reference, Instant now) {
        return new PaymentOrder(UUID.randomUUID(), debtorAccount, creditorAccount, amount, method, reference, now);
    }

    /** Call before asking a provider to move money, so a settled order is never charged twice. */
    public void ensureExecutable() {
        requireStatus(PaymentStatus.CREATED, "execute");
    }

    /** Call before asking a provider to refund, so only settled money is ever sent back. */
    public void ensureRefundable() {
        requireStatus(PaymentStatus.SETTLED, "refund");
    }

    public void settle(String providerReference, Money fee, Instant now) {
        requireStatus(PaymentStatus.CREATED, "settle");
        this.providerReference = Objects.requireNonNull(providerReference, "providerReference");
        this.fee = Objects.requireNonNull(fee, "fee");
        moveTo(PaymentStatus.SETTLED, now);
    }

    public void fail(String reason, Instant now) {
        requireStatus(PaymentStatus.CREATED, "fail");
        this.failureReason = Objects.requireNonNull(reason, "reason");
        moveTo(PaymentStatus.FAILED, now);
    }

    public void cancel(Instant now) {
        requireStatus(PaymentStatus.CREATED, "cancel");
        moveTo(PaymentStatus.CANCELLED, now);
    }

    public void refund(String refundReference, Instant now) {
        requireStatus(PaymentStatus.SETTLED, "refund");
        this.refundReference = Objects.requireNonNull(refundReference, "refundReference");
        moveTo(PaymentStatus.REFUNDED, now);
    }

    private void requireStatus(PaymentStatus required, String action) {
        if (status != required) {
            throw new IllegalPaymentStateException(id, action, status, required);
        }
    }

    private void moveTo(PaymentStatus next, Instant now) {
        this.status = next;
        this.updatedAt = Objects.requireNonNull(now, "now");
    }

    public UUID id() {
        return id;
    }

    public String debtorAccount() {
        return debtorAccount;
    }

    public String creditorAccount() {
        return creditorAccount;
    }

    public Money amount() {
        return amount;
    }

    public PaymentMethod method() {
        return method;
    }

    public String reference() {
        return reference;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public PaymentStatus status() {
        return status;
    }

    public Money fee() {
        return fee;
    }

    public String providerReference() {
        return providerReference;
    }

    public String refundReference() {
        return refundReference;
    }

    public String failureReason() {
        return failureReason;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
