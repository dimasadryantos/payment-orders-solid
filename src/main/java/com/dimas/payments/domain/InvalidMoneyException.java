package com.dimas.payments.domain;

public class InvalidMoneyException extends IllegalArgumentException {

    public InvalidMoneyException(String message) {
        super(message);
    }
}
