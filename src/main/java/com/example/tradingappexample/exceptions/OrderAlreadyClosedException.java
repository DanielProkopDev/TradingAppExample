package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class OrderAlreadyClosedException extends OrderDomainException{
    public OrderAlreadyClosedException(UUID orderId) {
        super("Order " + orderId + " is already closed");
    }
}
