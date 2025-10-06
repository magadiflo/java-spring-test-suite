package dev.magadiflo.app.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequest(@NotBlank
                             @Size(max = 100)
                             String holder,

                             @NotNull
                             @Min(0)
                             @Digits(integer = 17, fraction = 2)
                             BigDecimal balance) {
}
