package dev.magadiflo.app.exception;

public class BankNotFoundException extends EntityNotFoundException {
    public BankNotFoundException(Long bankId) {
        super("No se encontró el banco con ID: %d".formatted(bankId));
    }

    public BankNotFoundException(String bankName) {
        super("No se encontró el banco con nombre: %s".formatted(bankName));
    }
}
