package com.dimas.payments.api;

import com.dimas.payments.application.service.PaymentOrderNotFoundException;
import com.dimas.payments.application.service.RefundNotSupportedException;
import com.dimas.payments.application.validation.PaymentRuleViolationException;
import com.dimas.payments.domain.IllegalPaymentStateException;
import com.dimas.payments.domain.InvalidMoneyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Turns domain and application errors into RFC 9457 problem responses. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    ProblemDetail notFound(PaymentOrderNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Payment order not found", ex.getMessage());
    }

    @ExceptionHandler(IllegalPaymentStateException.class)
    ProblemDetail wrongState(IllegalPaymentStateException ex) {
        return problem(HttpStatus.CONFLICT, "Payment order is in the wrong state", ex.getMessage());
    }

    @ExceptionHandler(PaymentRuleViolationException.class)
    ProblemDetail ruleViolation(PaymentRuleViolationException ex) {
        ProblemDetail problem = problem(HttpStatus.UNPROCESSABLE_ENTITY, "Payment order rejected", ex.getMessage());
        problem.setProperty("violations", ex.violations());
        return problem;
    }

    @ExceptionHandler(RefundNotSupportedException.class)
    ProblemDetail refundNotSupported(RefundNotSupportedException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Refund not supported", ex.getMessage());
    }

    @ExceptionHandler(InvalidMoneyException.class)
    ProblemDetail invalidMoney(InvalidMoneyException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid amount or currency", ex.getMessage());
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
