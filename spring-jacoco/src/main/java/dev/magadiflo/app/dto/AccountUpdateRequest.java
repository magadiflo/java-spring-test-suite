package dev.magadiflo.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(@NotBlank(message = "El nombre del titular no puede estar vacío")
                                   @Size(max = 100, message = "El nombre del titular no puede superar los 100 caracteres")
                                   String holder) {
}
