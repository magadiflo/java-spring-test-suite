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

        assertEquals("Martín", account.getPerson());        // con JUnit
        assertThat(account.getPerson()).isEqualTo("Martín");// con AssertJ
    }
}
