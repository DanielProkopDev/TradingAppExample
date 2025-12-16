package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class OrderAlreadyCancelledException extends OrderDomainException{
    public OrderAlreadyCancelledException(UUID orderId) {
        super("Order" + orderId + " is already cancelled");
    }
}
