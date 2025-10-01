package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Lec01ConditionalsTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01ConditionalsTest.class);

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void shouldRunOnlyOnWindowsOs() {
        log.info("Ejecutando test para Windows");
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void shouldRunOnlyOnLinuxOs() {
        log.info("Ejecutando test para Linux");
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void shouldNotRunOnWindowsOs() {
        log.info("Este test no se está ejecutando en Windows");
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void shouldRunOnlyOnJava21Runtime() {
        log.info("Este test solo debe ejecutarse si usa java 21");
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void shouldRunOnlyOnJava17Runtime() {
        log.info("Este test solo debe ejecutarse si usa java 17");
    }

}
