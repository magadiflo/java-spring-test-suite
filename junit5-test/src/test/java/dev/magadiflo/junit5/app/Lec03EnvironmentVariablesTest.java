package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class Lec03EnvironmentVariablesTest {

    private static final Logger log = LoggerFactory.getLogger(Lec03EnvironmentVariablesTest.class);

    @Test
    void shouldPrintAllEnvironmentVariables() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((key, value) -> log.info("{}: {}", key, value));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\\\Program Files\\\\Java\\\\jdk-21.0.6")
    void shouldRunOnlyWhenJavaHomeIsSetToJdk_21_0_6() {
        log.info("Ejecutando test porque cumple la condición de la variable de entorno");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
    void shouldRunOnlyWhenSystemHasEightProcessors() {
        log.info("Ejecutando test solo si tiene 8 procesadores");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
    void shouldRunOnlyWhenEnvironmentVariableIsDev() {
        log.info("Ejecutando test solo si la variable de entorno ENV del sistema operativo es dev");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "prod")
    void shouldRunOnlyWhenEnvironmentVariableIsProd() {
        log.info("Ejecutando test solo si la variable de entorno ENV del sistema operativo es prod");
    }
}