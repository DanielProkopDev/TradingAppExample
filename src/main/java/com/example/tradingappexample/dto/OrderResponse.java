package com.example.tradingappexample.dto;

import com.example.tradingappexample.dao.Order;
import com.example.tradingappexample.dao.OrderStatus;
import com.example.tradingappexample.dao.Side;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String symbol,
        Side side,
        BigDecimal quantity,
        BigDecimal filledQuantity,
        OrderStatus status,
        Long version
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getSide(),
                order.getQuantity(),
                order.getFilledQuantity(),
                order.getStatus(),
                order.getVersion()
        );
    }
}
