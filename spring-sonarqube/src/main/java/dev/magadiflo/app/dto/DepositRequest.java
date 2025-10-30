package dev.magadiflo.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(@NotNull(message = "Debe especificar el monto a depositar")
                             @DecimalMin(value = "0.01", message = "El monto mínimo es 0.01")
                             @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 dígitos enteros y 2 decimales")
                             BigDecimal amount) {
}
