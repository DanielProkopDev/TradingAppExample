package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class OrderNotFoundException extends OrderDomainException
{
    public OrderNotFoundException(UUID id)
    {
        super("Order " +id + " not found");
    }
}
