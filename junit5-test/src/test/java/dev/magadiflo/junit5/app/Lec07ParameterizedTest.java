package dev.magadiflo.junit5.app;

import dev.magadiflo.junit5.app.model.Account;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

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

    @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000", "7,2000"})
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalanceCsvSource(String index, String amount) {
        log.info("{}: {}", index, amount);

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
    @CsvFileSource(resources = "/csv/data.csv")
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalanceCsvFileSource(String amount) {
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
    @MethodSource("amountList")
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalanceMethodSource(String amount) {
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
    @CsvFileSource(resources = "/csv/data-multiple-values.csv")
    void shouldDebitAccountWithVariousAmountsAndValidatePositiveBalanceCsvFileSource(String balance, String amount, String expected, String actual) {
        Account account = new Account("Martín", new BigDecimal(balance));
        account.debit(new BigDecimal(amount));
        account.setPerson(actual);

        // JUnit 5
        assertNotNull(account.getBalance());
        assertNotNull(account.getPerson());
        assertEquals(expected, account.getPerson());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(account.getPerson())
                .isNotNull()
                .isEqualTo(expected);
    }

    private static List<String> amountList() {
        return List.of("100", "200", "300", "500", "700", "1000", "2000");
    }
}
