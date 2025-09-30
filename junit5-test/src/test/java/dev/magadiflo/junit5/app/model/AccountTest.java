package dev.magadiflo.junit5.app.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldReturnCorrectPersonNameWhenAccountIsCreated() {
        Account account = new Account("Martín", new BigDecimal("2000"));

        String real = account.getPerson();

        // JUnit 5
        assertEquals("Martín", real);

        // AssertJ
        assertThat(real).isEqualTo("Martín");
    }

    @Test
    void shouldHavePositiveBalanceWhenAccountIsCreated() {
        Account account = new Account("Martín", new BigDecimal("2000"));

        // JUnit 5
        assertEquals(2000D, account.getBalance().doubleValue());
        assertNotEquals(-1, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(1, account.getBalance().compareTo(BigDecimal.ZERO));

        // AssertJ
        assertThat(account.getBalance()).isEqualByComparingTo("2000");
        assertThat(account.getBalance().compareTo(BigDecimal.ZERO)).isNotEqualTo(-1);
        assertThat(account.getBalance().compareTo(BigDecimal.ZERO)).isGreaterThan(0);
    }
}
