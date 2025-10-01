package dev.magadiflo.junit5.app;

import dev.magadiflo.junit5.app.model.Account;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Lec04AssumptionsProgrammaticallyTest {

    private static final Logger log = LoggerFactory.getLogger(Lec04AssumptionsProgrammaticallyTest.class);

    @Test
    void shouldRunBalanceAccountTestOnlyIfEnvIsDev() {
        boolean isDev = "dev".equals(System.getenv("ENV"));
        Assumptions.assumeTrue(isDev); //Si es true, continuamos con la ejecución del test, caso contrario queda como deshabilitado

        Account account = new Account("Martín", new BigDecimal("2000"));

        // JUnit 5
        assertEquals("2000", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .withFailMessage("El saldo inicial no coincide con el esperado")
                .isEqualByComparingTo("2000");
    }

    @Test
    void shouldRunBalanceAssertionOnlyIfEnvIsQa() {
        boolean isDev = "qa".equals(System.getenv("ENV"));

        // JUnit 5
        Assumptions.assumingThat(isDev, () -> {
            Account account = new Account("Martín", new BigDecimal("2000"));

            // AssertJ
            assertThat(account.getBalance())
                    .withFailMessage("El saldo inicial no coincide con el esperado")
                    .isEqualByComparingTo("2000");
        });

        log.info("El test continuó ejecutándose aunque la condición no se cumpliera");
    }
}
