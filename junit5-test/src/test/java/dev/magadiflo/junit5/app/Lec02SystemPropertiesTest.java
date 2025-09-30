package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

class Lec02SystemPropertiesTest {

    private static final Logger log = LoggerFactory.getLogger(Lec02SystemPropertiesTest.class);

    @Test
    void shouldPrintAllSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((key, value) -> log.info("{}: {}", key, value));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "21.0.6")
    void shouldRunOnlyOnExactJavaVersion_21_0_6() {
        log.info("Ejecutando test para la versión exacta de java 21.0.6");
    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void shouldNotRunOn32BitArchitecture() {
        log.info("Solo se ejecutará si la arquitectura del SO no es de 32bits");
    }

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void shouldRunOnlyWhenEnvPropertyIsDev() {
        log.info("Test ejecutado solo si existe la propiedad de sistema ENV con valor dev");
    }

}
