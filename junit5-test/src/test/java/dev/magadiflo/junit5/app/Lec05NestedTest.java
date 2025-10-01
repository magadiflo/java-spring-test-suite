package dev.magadiflo.junit5.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Lec05NestedTest {

    private static final Logger log = LoggerFactory.getLogger(Lec05NestedTest.class);

    @Nested
    class OperatingSystemTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void shouldRunOnlyOnWindowsOs() {
            log.info("Ejecutando test para Windows");
        }

        @Test
        @EnabledOnOs(value = OS.LINUX, disabledReason = "Test que se ejecuta solo en Linux")
        void shouldRunOnlyOnLinuxOs() {
            log.info("Ejecutando test para Linux");
        }

        @Test
        @DisabledOnOs(value = OS.WINDOWS, disabledReason = "Si es windows este test se deshabilitará")
        void shouldNotRunOnWindowsOs() {
            log.info("Este test no se ejecuta en Windows");
        }
    }

    @Nested
    class JavaVersionTest {

        @Test
        @Disabled
        void failTheTest() {
            // JUnit 5
            Assertions.fail("Fallando para ver el comportamiento");
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void shouldRunOnlyOnJava8Runtime() {
            log.info("Test que se ejecuta solo si usa java 8");
        }

        @Test
        @EnabledOnJre(JRE.JAVA_21)
        void shouldRunOnlyOnJava21Runtime() {
            log.info("Test que se ejecuta solo si usa java 21");
        }
    }

    @Nested
    class SystemPropertiesTest {

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "17.0.4.1")
        void shouldRunOnlyWhenJavaVersionIs_17_0_4_1() {
            log.info("Ejecutando test para la versión exacta de java 17.0.4.1");
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void shouldNotRunOn32BitArchitecture() {
            log.info("Solo se ejecutará si la arquitectura del SO no es de 32bits");
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void shouldRunOnlyWhenEnvPropertyIsDev() {
            log.info("Test ejecutado solo si existe la propiedad de sistema DEV con valor dev");
        }
    }

    @Nested
    class EnvironmentVariablesTest {

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\\\Program Files\\\\Java\\\\jdk-17.0.4.1")
        void shouldRunOnlyWhenJavaHomeIsSetToJdk_17_0_4_1() {
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
            log.info("Ejecutando test solo si su variable de entorno del SO es dev");
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENV", matches = "prod")
        void shouldRunOnlyWhenEnvironmentVariableIsProd() {
            log.info("Ejecutando test solo si su variable de entorno del SO es prod");
        }
    }
}
