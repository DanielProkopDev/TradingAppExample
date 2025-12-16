package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class OrderAlreadyFilledException extends OrderDomainException{
    public OrderAlreadyFilledException(UUID orderId) {
        super("Order" + orderId + " is already filled");
    }
}
