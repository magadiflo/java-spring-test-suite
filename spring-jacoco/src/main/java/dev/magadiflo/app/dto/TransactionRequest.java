package dev.magadiflo.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(@NotNull(message = "Debe especificarse el ID de la cuenta de origen")
                                 @Positive(message = "El ID de la cuenta origen debe ser un número positivo")
                                 Long sourceAccountId,

                                 @NotNull(message = "Debe especificarse el ID de la cuenta de destino")
                                 @Positive(message = "El ID de la cuenta destino debe ser un número positivo")
                                 Long targetAccountId,

                                 @NotNull(message = "Debe especificar el monto a transferir")
                                 @DecimalMin(value = "0.01", message = "El monto mínimo de transferencia es 0.01")
                                 @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 dígitos enteros y 2 decimales")
                                 BigDecimal amount) {
}
