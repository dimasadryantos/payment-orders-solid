package com.dimas.payments.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    @Test
    void normalizesToTheScaleOfTheCurrency() {
        assertEquals(new BigDecimal("10.50"), Money.of("10.5", "EUR").amount());
        assertEquals(new BigDecimal("500"), Money.of("500", "JPY").amount());
    }

    @Test
    void rejectsMoreDecimalsThanTheCurrencyAllows() {
        assertThrows(InvalidMoneyException.class, () -> Money.of("10.555", "EUR"));
        assertThrows(InvalidMoneyException.class, () -> Money.of("10.5", "JPY"));
    }

    @Test
    void rejectsNegativeAmountsAndUnknownCurrencies() {
        assertThrows(InvalidMoneyException.class, () -> Money.of("-1.00", "EUR"));
        assertThrows(InvalidMoneyException.class, () -> Money.of("1.00", "EURO"));
        assertThrows(InvalidMoneyException.class, () -> Money.of("abc", "EUR"));
    }

    @Test
    void computesPercentagesWithBankersRounding() {
        assertEquals(Money.of("1.40", "EUR"), Money.of("100.00", "EUR").percentage(new BigDecimal("1.4")));
        // 1.4 % of 12.50 is 0.175, which rounds half-even to 0.18
        assertEquals(Money.of("0.18", "EUR"), Money.of("12.50", "EUR").percentage(new BigDecimal("1.4")));
        // 1.4 % of 17.50 is 0.245, which rounds half-even to 0.24
        assertEquals(Money.of("0.24", "EUR"), Money.of("17.50", "EUR").percentage(new BigDecimal("1.4")));
    }

    @Test
    void addsOnlyWithinOneCurrency() {
        assertEquals(Money.of("1.65", "EUR"), Money.of("1.40", "EUR").plus(Money.of("0.25", "EUR")));
        assertThrows(InvalidMoneyException.class, () -> Money.of("1.00", "EUR").plus(Money.of("1.00", "USD")));
    }

    @Test
    void comparesAgainstALimit() {
        assertTrue(Money.of("5000.01", "EUR").isGreaterThan(new BigDecimal("5000")));
        assertTrue(!Money.of("5000.00", "EUR").isGreaterThan(new BigDecimal("5000")));
    }
}
