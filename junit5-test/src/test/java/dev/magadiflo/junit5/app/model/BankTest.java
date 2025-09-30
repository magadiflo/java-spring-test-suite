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

    @Test
    void shouldAssociateAccountsWithBankCorrectly() {
        Account source = new Account("Martín", new BigDecimal("2000.50"));
        Account target = new Account("Alicia", new BigDecimal("1500.50"));

        Bank bank = new Bank();
        bank.setName("Banco BBVA");
        bank.addAccount(source);
        bank.addAccount(target);

        // JUnit 5
        assertEquals(2, bank.getAccounts().size(), "El banco debe tener 2 cuentas");

        // AssertJ
        assertThat(bank.getAccounts())
                .hasSize(2)
                .containsExactly(source, target);
    }

    @Test
    void shouldLinkAccountsToBankAndReflectOwnershipCorrectly() {
        Account source = new Account("Martín", new BigDecimal("2000.50"));
        Account target = new Account("Alicia", new BigDecimal("1500.50"));

        Bank bank = new Bank();
        bank.setName("Banco BBVA");
        bank.addAccount(source);
        bank.addAccount(target);

        // JUnit 5
        assertEquals(2, bank.getAccounts().size());
        assertEquals("Banco BBVA", source.getBank().getName());
        assertEquals("Banco BBVA", target.getBank().getName());
        assertTrue(bank.getAccounts().stream().anyMatch(a -> a.getPerson().equals("Martín")));

        // AssertJ
        assertThat(bank.getAccounts()).hasSize(2);
        assertThat(source.getBank().getName()).isEqualTo("Banco BBVA");
        assertThat(target.getBank().getName()).isEqualTo("Banco BBVA");
        assertThat(bank.getAccounts()).anyMatch(account -> account.getPerson().equals("Martín"));
    }

    @Test
    void shouldValidateBankAccountRelationshipsCollectively() {
        Account account1 = new Account("Martín", new BigDecimal("2000.50"));
        Account account2 = new Account("Alicia", new BigDecimal("1500.50"));
        Account account3 = new Account("Alex", new BigDecimal("1500.50"));

        Bank bank = new Bank();
        bank.setName("Banco BBVA");
        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.addAccount(account3);

        // JUnit 5: agrupando asserts
        assertAll(
                () -> assertEquals(3, bank.getAccounts().size()),
                () -> assertEquals("Banco BBVA", account2.getBank().getName()),
                () -> assertTrue(bank.getAccounts().stream().anyMatch(a -> a.getPerson().equals("Alex")))
        );

        // JUnit 5 + AssertJ dentro del assertAll
        assertAll(
                () -> assertThat(bank.getAccounts()).hasSize(3),
                () -> assertThat(account1.getBalance()).isNotNull(),
                () -> assertThat(account1.getBalance()).isEqualByComparingTo("2000.50"),
                () -> assertThat(account1.getPerson()).isEqualTo("Martín")
        );
    }
}
