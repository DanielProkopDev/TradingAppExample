package com.example.tradingappexample.dto;

import com.example.tradingappexample.dao.Side;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank String symbol,
        @NotNull Side side,
        @NotNull @Positive BigDecimal quantity
) {
}
