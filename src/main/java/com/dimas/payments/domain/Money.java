package com.dimas.payments.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * An amount in a currency. Always BigDecimal, never double, and always at the
 * scale the currency defines (2 for EUR, 0 for JPY).
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null) {
            throw new InvalidMoneyException("Amount is required");
        }
        if (currency == null) {
            throw new InvalidMoneyException("Currency is required");
        }
        int fractionDigits = currency.getDefaultFractionDigits();
        if (fractionDigits < 0) {
            throw new InvalidMoneyException("Currency " + currency + " cannot be used for payments");
        }
        if (amount.signum() < 0) {
            throw new InvalidMoneyException("Amount must not be negative");
        }
        try {
            amount = amount.setScale(fractionDigits, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new InvalidMoneyException(
                    "Amount %s has more decimal places than %s allows".formatted(amount.toPlainString(), currency));
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        if (currencyCode == null) {
            throw new InvalidMoneyException("Currency is required");
        }
        try {
            return new Money(amount, Currency.getInstance(currencyCode));
        } catch (InvalidMoneyException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new InvalidMoneyException("Unknown currency: " + currencyCode);
        }
    }

    public static Money of(String amount, String currencyCode) {
        try {
            return of(new BigDecimal(amount), currencyCode);
        } catch (NumberFormatException | NullPointerException e) {
            throw new InvalidMoneyException("Not a valid amount: " + amount);
        }
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money plus(Money other) {
        if (!currency.equals(other.currency)) {
            throw new InvalidMoneyException("Cannot add " + other.currency + " to " + currency);
        }
        return new Money(amount.add(other.amount), currency);
    }

    /** The given percentage of this amount, rounded half-even to the currency scale. */
    public Money percentage(BigDecimal percent) {
        BigDecimal share = amount.multiply(percent)
                .divide(BigDecimal.valueOf(100), currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
        return new Money(share, currency);
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(BigDecimal threshold) {
        return amount.compareTo(threshold) > 0;
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCurrencyCode();
    }
}
