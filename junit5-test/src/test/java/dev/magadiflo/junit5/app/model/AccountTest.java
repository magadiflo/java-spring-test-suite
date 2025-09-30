package dev.magadiflo.junit5.app.model;

import dev.magadiflo.junit5.app.exception.InsufficientMoneyException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private static final Logger log = LoggerFactory.getLogger(AccountTest.class);
    private Account account;

    @BeforeEach
    void setUp() {
        log.info("Ejecutando @BeforeEach - iniciando recursos");
        this.account = new Account("Martín", new BigDecimal("2000"));
    }

    @AfterEach
    void tearDown() {
        this.account = null;
        log.info("Ejecutando @AfterEach - recursos liberados");
    }

    @Test
    void shouldReturnCorrectPersonNameWhenAccountIsCreated() {
        String real = account.getPerson();

        // JUnit 5
        assertEquals("Martín", real);

        // AssertJ
        assertThat(real).isEqualTo("Martín");
    }

    @Test
    void shouldHavePositiveBalanceWhenAccountIsCreated() {
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
    @Disabled("Se deshabilitó porque equals() fue sobreescrito y la comparación por referencia ya no aplica")
    void shouldNotBeSameReferenceWhenAccountAreCreatedSeparately() {
        Account account1 = new Account("Liz Gonzales", new BigDecimal("2500.00"));
        Account account2 = new Account("Liz Gonzales", new BigDecimal("2500.00"));

        // JUnit 5
        assertNotEquals(account1, account2); // Lo comentamos porque ya sobreescribimos equals(), ahora ambos objetos son considerados iguales por valor.

        // AssertJ
        assertThat(account1).isNotSameAs(account2);
    }

    @Test
    @DisplayName("Verifying that two objects are equal")
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
        Account account1 = new Account("Martín", new BigDecimal("2001"));
        account1.credit(new BigDecimal("100"));

        // JUnit 5
        assertNotNull(account1.getBalance(), () -> "La cuenta no puede ser nula");
        assertEquals(2101D, account1.getBalance().doubleValue(), () -> "El valor obtenido no es igual al valor que se espera");
        assertEquals("2101", account1.getBalance().toPlainString(), () -> "El valor obtenido no es igual al valor que se espera");

        // AssertJ
        assertThat(account1.getBalance())
                .withFailMessage(() -> "El saldo no coincide con el esperado")
                .isEqualByComparingTo("2101");
    }

}
