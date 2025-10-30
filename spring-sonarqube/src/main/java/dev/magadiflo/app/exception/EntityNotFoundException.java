package dev.magadiflo.app.exception;

/**
 * Excepción base para entidades no encontradas en el sistema.
 * <p>
 * Sirve como padre para excepciones más específicas como
 * {@link AccountNotFoundException} y {@link BankNotFoundException}.
 * </p>
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
