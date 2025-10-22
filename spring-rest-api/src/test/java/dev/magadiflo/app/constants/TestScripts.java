package dev.magadiflo.app.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestScripts {
    // Limpieza por BD
    public static final String CLEANUP_H2 = "/sql-test/cleanup-h2.sql";
    public static final String CLEANUP_MYSQL = "/sql-test/cleanup-mysql.sql";

    // Datos compartidos
    public static final String DATA_TEST = "/sql-test/data-test.sql";

    // Combinación comunes
    public static final String[] H2_INIT = {CLEANUP_H2, DATA_TEST};
    public static final String[] MYSQL_INIT = {CLEANUP_MYSQL, DATA_TEST};
}

