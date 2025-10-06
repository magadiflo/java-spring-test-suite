package dev.magadiflo.app.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(@NotNull
                                 @Positive
                                 Long bankId,

                                 @NotNull
                                 @Positive
                                 Long sourceAccountId,

                                 @NotNull
                                 @Positive
                                 Long targetAccountId,

                                 @NotNull
                                 @Positive
                                 @Digits(integer = 17, fraction = 2)
                                 BigDecimal amount) {
}
