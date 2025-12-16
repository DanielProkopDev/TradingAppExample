package com.example.tradingappexample.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record FillOrderRequest(
        @NotNull @Positive BigDecimal quantity
) {
}
