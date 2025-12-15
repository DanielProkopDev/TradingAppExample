package com.example.tradingappexample.dto;

public record TradeResult(
        TradeResponse trade,
        boolean replayed
) {
}
