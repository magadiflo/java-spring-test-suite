package dev.magadiflo.app.exception;

public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String operation) {
        super("Error al ejecutar operación de BD: %s. No se afectaron las filas esperadas".formatted(operation));
    }

    public DatabaseOperationException(String operation, Throwable cause) {
        super("Error al ejecutar operación de BD: %s".formatted(operation), cause);
    }
}
