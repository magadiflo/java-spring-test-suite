package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Lec08TagTest {

    private static final Logger log = LoggerFactory.getLogger(Lec08TagTest.class);

    @Tag("param")
    @Test
    void shouldRunTaggedParamTest_1() {
        log.info("Ejecutando test 1 con tag param");
    }

    @Tag("param")
    @Test
    void shouldRunTaggedParamTest_2() {
        log.info("Ejecutando test 2 con tag param");
    }

    @Test
    void shouldRunUntaggedTestForParamCategory() {
        log.info("Este test unitario no tiene el tag param!");
    }

    @Tag("account")
    @Test
    void shouldRunTaggedAccountTest_1() {
        log.info("Ejecutando test de cuenta 1");
    }

    @Tag("account")
    @Test
    void shouldRunTaggedAccountTest_2() {
        log.info("Ejecutando test de cuenta 2");
    }
}
