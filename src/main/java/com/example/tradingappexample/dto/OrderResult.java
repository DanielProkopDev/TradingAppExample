package com.example.tradingappexample.dto;

public record OrderResult(
        OrderResponse order,
        boolean replayed
) {
}
