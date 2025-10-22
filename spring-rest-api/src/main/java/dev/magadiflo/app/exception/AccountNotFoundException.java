package dev.magadiflo.app.exception;

public class AccountNotFoundException extends EntityNotFoundException {
    public AccountNotFoundException(Long accountId) {
        super("No se encontró la cuenta con ID: %d".formatted(accountId));
    }

    public AccountNotFoundException(String holder) {
        super("No se encontró la cuenta del titular: %s".formatted(holder));
    }
}
