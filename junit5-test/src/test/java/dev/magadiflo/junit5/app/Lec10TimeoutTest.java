package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class Lec10TimeoutTest {

    private static final Logger log = LoggerFactory.getLogger(Lec10TimeoutTest.class);

    @Test
    @Timeout(5)
    void shouldFailIfExecutionExceedsFiveSecons() throws InterruptedException {
        // Simula una operación que excede el límite de tiempo (segundos)
        TimeUnit.SECONDS.sleep(6);
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    void shouldFailIfExecutionExceedsHalfASecond() throws InterruptedException {
        // Simula una operación que excede el límite de tiempo (milisegundos)
        Thread.sleep(Duration.ofMillis(510));
    }

    @Test
    void shouldFailIfCodeExecutionExceedsFiveSeconds() {
        // JUnit 5
        assertTimeout(Duration.ofSeconds(5), () -> {
            TimeUnit.SECONDS.sleep(6);  //<-- Simula la demora de nuestro test
        }, "El bloque de código debería completarse en menos de 5 segundos");
    }
}
