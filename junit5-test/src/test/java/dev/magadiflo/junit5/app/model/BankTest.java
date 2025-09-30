package dev.magadiflo.junit5.app.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void shouldTransferMoneyBetweenAccountsCorrectly() {
        Account source = new Account("Martín", new BigDecimal("2000.50"));
        Account target = new Account("Alicia", new BigDecimal("1500.50"));

        Bank bank = new Bank();
        bank.setName("Banco BBVA");

        bank.transfer(source, target, new BigDecimal("500.50"));

        // JUnit 5
        assertEquals(1500D, source.getBalance().doubleValue());
        assertEquals(2001D, target.getBalance().doubleValue());

        // AssertJ
        assertThat(source.getBalance()).isEqualByComparingTo("1500");
        assertThat(target.getBalance()).isEqualByComparingTo("2001");
    }
}