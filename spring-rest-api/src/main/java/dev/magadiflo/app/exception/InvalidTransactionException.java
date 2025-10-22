package dev.magadiflo.app.exception;

/**
 * Excepción lanzada cuando una transacción no cumple con las reglas de negocio.
 * <p>
 * Ejemplos de uso:
 * <ul>
 *   <li>Transferencia entre cuentas de diferentes bancos</li>
 *   <li>Transferencia de una cuenta a sí misma</li>
 *   <li>Monto de transferencia inválido</li>
 * </ul>
 * </p>
 */
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
