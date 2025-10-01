package dev.magadiflo.junit5.app;

import dev.magadiflo.junit5.app.model.Account;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec09InfoReporterTest {

    private static final Logger log = LoggerFactory.getLogger(Lec09InfoReporterTest.class);
    private Account account;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.account = new Account("Martín", new BigDecimal("2000"));

        log.info("{}", testInfo.getDisplayName());
        testInfo.getTestMethod().ifPresent(method -> log.info("{}", method.getName()));
        testInfo.getTestClass().ifPresent(aClass -> log.info("{}", aClass.getName()));
        log.info("{}", testInfo.getTags());

        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName());
    }

    @Test
    @Tag("account")
    @DisplayName("Probando nombre de la cuenta")
    void shouldReturnCorrectAccountHolderName() {
        String expected = "Martín";
        String real = this.account.getPerson();

        // JUnit 5
        assertEquals(expected, real);

        // AssertJ
        assertThat(real).isEqualTo(expected);
    }
}
