package dev.magadiflo.app.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId, String holder) {
        super("Saldo insuficiente en la cuenta del titular %s (ID: %d)".formatted(holder, accountId));
    }
}
