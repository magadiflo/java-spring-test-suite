package dev.magadiflo.junit5.app;

import dev.magadiflo.junit5.app.model.Account;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Lec06RepeatedTest {

    private static final Logger log = LoggerFactory.getLogger(Lec06RepeatedTest.class);

    @RepeatedTest(value = 5)
    void shouldDebitAccountAndValidateBalanceRepeatedly() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal("100"));

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(1900D, account.getBalance().doubleValue());
        assertEquals("1900", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .withFailMessage("El saldo no debería ser nulo después del débito")
                .isNotNull()
                .withFailMessage("El saldo numérico no coincide con el esperado")
                .isEqualByComparingTo("1900");
    }

    @RepeatedTest(value = 5, name = "Repetición número {currentRepetition} de {totalRepetitions}")
    void shouldDebitAccountAndValidateBalanceRepeatedly(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            log.info("Estamos en la repetición {}", info.getCurrentRepetition());
        }

        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal("100"));

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(1900D, account.getBalance().doubleValue());
        assertEquals("1900", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .withFailMessage("El saldo no debería ser nulo después del débito")
                .isNotNull()
                .withFailMessage("El saldo numérico no coincide con el esperado")
                .isEqualByComparingTo("1900");
    }
}
