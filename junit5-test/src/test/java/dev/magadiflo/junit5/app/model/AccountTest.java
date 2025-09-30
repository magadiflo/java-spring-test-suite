package dev.magadiflo.junit5.app.model;

import dev.magadiflo.junit5.app.exception.InsufficientMoneyException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void shouldNotBeSameReferenceWhenAccountAreCreatedSeparately() {
        Account account1 = new Account("Liz Gonzales", new BigDecimal("2500.00"));
        Account account2 = new Account("Liz Gonzales", new BigDecimal("2500.00"));

        // JUnit 5
        //assertNotEquals(account1, account2); // Lo comentamos porque ya sobreescribimos equals(), ahora ambos objetos son considerados iguales por valor.

        // AssertJ
        assertThat(account1).isNotSameAs(account2);
    }

    @Test
    void shouldBeEqualWhenAccountsHaveSameValues() {
        Account account1 = new Account("Liz Gonzales", new BigDecimal("2500.00"));
        Account account2 = new Account("Liz Gonzales", new BigDecimal("2500.00"));

        // JUnit 5
        assertEquals(account1, account2);

        // AssertJ
        assertThat(account1).isEqualTo(account2);
    }

    @Test
    void shouldReduceBalanceWhenDebitIsApplied() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal("100")); // ejecutamos el método a probar

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(1900D, account.getBalance().doubleValue());
        assertEquals("1900", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isEqualByComparingTo("1900");
    }

    @Test
    void shouldIncreaseBalanceWhenCreditIsApplied() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.credit(new BigDecimal("100")); // ejecutamos el método a probar

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(2100D, account.getBalance().doubleValue());
        assertEquals("2100", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isEqualByComparingTo("2100");
    }

    @Test
    void shouldThrowInsufficientMoneyExceptionWhenDebitExceedsBalance() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        BigDecimal amount = new BigDecimal("5000");

        // JUnit 5
        InsufficientMoneyException exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(amount);
        }, "Se esperaba que InsufficientMoneyException fuera lanzado"); //<-- Nuestro mensaje a mostrar cuando falle
        assertEquals(InsufficientMoneyException.class, exception.getClass());
        assertEquals("Dinero insuficiente", exception.getMessage());

        // AssertJ
        assertThatThrownBy(() -> account.debit(amount))
                .isInstanceOf(InsufficientMoneyException.class)
                .hasMessage("Dinero insuficiente");
    }

    @Test
    void shouldCreditAccountAndReflectUpdatedBalance() {
        Account account = new Account("Martín", new BigDecimal("2001"));
        account.credit(new BigDecimal("100"));

        // JUnit 5
        assertNotNull(account.getBalance(), () -> "La cuenta no puede ser nula");
        assertEquals(2101D, account.getBalance().doubleValue(), () -> "El valor obtenido no es igual al valor que se espera");
        assertEquals("2101", account.getBalance().toPlainString(), () -> "El valor obtenido no es igual al valor que se espera");

        // AssertJ
        assertThat(account.getBalance())
                .withFailMessage(() -> "El saldo no coincide con el esperado")
                .isEqualByComparingTo("2101");
    }

}
