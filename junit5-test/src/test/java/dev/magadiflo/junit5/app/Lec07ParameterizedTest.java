package dev.magadiflo.junit5.app;

import dev.magadiflo.junit5.app.model.Account;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Lec07ParameterizedTest {

    private static final Logger log = LoggerFactory.getLogger(Lec07ParameterizedTest.class);

    @ParameterizedTest
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000", "2000"})
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalance(String amount) {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal(amount));

        // JUnit 5
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isGreaterThan(BigDecimal.ZERO);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000", "2000"})
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalance2(String amount) {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal(amount));

        // JUnit 5
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isGreaterThan(BigDecimal.ZERO);
    }
}
