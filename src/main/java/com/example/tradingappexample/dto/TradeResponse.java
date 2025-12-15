package com.example.tradingappexample.dto;

import com.example.tradingappexample.dao.Side;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID id,
        String symbol,
        Side side,
        BigDecimal quantity,
        BigDecimal price,
        Instant createdAt

) {
}
