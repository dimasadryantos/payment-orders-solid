# payment-orders-solid

A small Spring Boot service for payment orders, built to show each SOLID
principle in real code rather than in shapes-and-animals examples.

Java 21 · Spring Boot 3.5 · Maven · no database needed (orders are kept in memory)

## Run it

In IntelliJ: open the folder, let Maven import, then run `PaymentOrdersApplication`.
From a terminal: `mvn spring-boot:run`, tests with `mvn test`.

`requests.http` has ready-made calls you can run from IntelliJ (click the green
arrow next to each one).

## What it does

| Call | Effect |
|---|---|
| `POST /api/v1/payment-orders` | validate and create an order (`CREATED`) |
| `POST /api/v1/payment-orders/{id}/execute` | send it to the processor for its method (`SETTLED` or `FAILED`) |
| `POST /api/v1/payment-orders/{id}/cancel` | cancel before execution (`CANCELLED`) |
| `POST /api/v1/payment-orders/{id}/refund` | refund a settled order (`REFUNDED`), card and wallet only |
| `GET  /api/v1/payment-orders/{id}` | read it |

Three payment methods, all simulated:

| Method | Fee | Refundable | Notes |
|---|---|---|---|
| `SEPA_CREDIT_TRANSFER` | 0.20 flat | no | EUR only, limit 100,000 |
| `CARD` | 1.4 % + 0.25 | yes | EUR/USD/GBP, limit 5,000 |
| `WALLET` | none | yes | EUR only, limit 2,000; declines above 1,000 (simulated balance) |

Limits and currencies come from `application.yml`, not from code.

## Where each principle lives

### S: Single Responsibility
One class, one reason to change.

- `PaymentOrder` only knows the lifecycle (which status may follow which).
- Each use case has its own service: `CreatePaymentOrderService`, `ExecutePaymentOrderService`, `CancelPaymentOrderService`, `RefundPaymentOrderService`, `GetPaymentOrderService`.
- `PaymentOrderController` only translates HTTP, `ApiExceptionHandler` only maps errors, `Money` only does money arithmetic.

*In an interview:* "If the card fee changes I touch one class, and it isn't the one that handles HTTP or the one that stores orders."

### O: Open/Closed
Add behaviour by adding a class, not by editing one.

- `PaymentProcessor`: one implementation per payment method. `PaymentProcessorRegistry` receives every implementation from Spring as a list, so there is no `switch (method)` anywhere.
- `PaymentRule`: one class per business rule. `PaymentOrderValidator` receives them all as a list.
- `PaymentPolicy`: limits and currencies are configuration.

*Try it:* add a `PayPalProcessor` (`@Component`, implements `PaymentProcessor`) and a `PAYPAL` enum constant. No service, controller or registry changes.

### L: Liskov Substitution
Any implementation can replace another without the caller noticing.

- The contract is written on `PaymentProcessor`: never mutate the order, return a decline instead of throwing, always return a fee in the order's currency.
- `PaymentProcessorContractTest` checks that contract, and every processor's test extends it. A new processor that breaks the contract fails its own test.
- `PaymentResult` is a sealed type (`Approved` / `Declined`), so "declined" is a value every processor returns the same way.

*In an interview:* "SEPA can't be refunded. If I'd put `refund()` on the base interface, the SEPA processor would have to throw, and every caller would need a special case. That's the LSP violation I avoided."

### I: Interface Segregation
Don't make a client depend on methods it doesn't use.

- `RefundablePaymentProcessor` is a separate interface. Card and wallet implement it; SEPA doesn't.
- The controller depends on five one-method interfaces (`CreatePaymentOrderUseCase`, `ExecutePaymentOrderUseCase`, …), not one fat `PaymentService`.

### D: Dependency Inversion
High-level code depends on abstractions it owns.

- `application/port/out` holds the interfaces the services need: `PaymentOrderRepository`, `PaymentProcessor`, `PaymentEventPublisher`. The application layer defines them; `infrastructure` implements them.
- Nothing in `domain` or `application` imports from `infrastructure` or `api`.
- That is why the service tests need no Spring context and no mocking library: they pass in `InMemoryPaymentOrderRepository` and a hand-written `RecordingEventPublisher`.
- Even time is injected (`Clock`), so tests use a fixed instant.

*Try it:* replace `InMemoryPaymentOrderRepository` with a JPA class implementing the same port. No service changes.

## Layout

```
com.dimas.payments
├── domain            PaymentOrder, Money, PaymentStatus, PaymentMethod
├── application
│   ├── port.in       one interface per use case
│   ├── port.out      what the use cases need from the outside world
│   ├── validation    PaymentRule + the rules + PaymentPolicy
│   └── service       one service per use case, PaymentProcessorRegistry
├── infrastructure    processors, in-memory repository, event logger, Clock bean
└── api               controller, request/response records, error mapping
```

## Left out on purpose

This is a SOLID demo, so a real payment service would still need: idempotency
keys on create and execute, a real database with optimistic locking (two
concurrent `execute` calls on one order are not guarded here), an outbox for
events, and authentication.
