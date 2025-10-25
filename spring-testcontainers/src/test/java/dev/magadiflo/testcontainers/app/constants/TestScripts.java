package dev.magadiflo.testcontainers.app.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestScripts {
    // Limpieza de base de datos
    public static final String CLEANUP_POSTGRES = "/sql-test/cleanup-postgres.sql";

    // Datos de prueba comunes
    public static final String DATA_TEST = "/sql-test/data-test.sql";
}
