package dev.magadiflo.app.dto;

import java.math.BigDecimal;

public record AccountResponse(Long id,
                              String holder,
                              BigDecimal balance) {
}
