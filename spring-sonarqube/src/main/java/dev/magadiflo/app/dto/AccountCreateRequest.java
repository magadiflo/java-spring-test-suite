package dev.magadiflo.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountCreateRequest(@NotBlank(message = "El nombre del titular no puede estar vacío")
                                   @Size(max = 100, message = "El nombre del titular no puede superar los 100 caracteres")
                                   String holder,

                                   @NotNull(message = "El saldo inicial es obligatorio")
                                   @DecimalMin(value = "0.00", message = "El saldo no puede ser negativo")
                                   @Digits(integer = 17, fraction = 2, message = "El saldo debe tener hasta 17 dígitos enteros y 2 decimales")
                                   BigDecimal balance,

                                   @NotNull(message = "Debe especificarse el banco asociado")
                                   @Positive(message = "El identificador del banco debe ser un número positivo")
                                   Long bankId) {
}
