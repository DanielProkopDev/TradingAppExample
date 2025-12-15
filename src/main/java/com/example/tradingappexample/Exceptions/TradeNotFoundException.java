package com.example.tradingappexample.Exceptions;

import java.util.UUID;

public class TradeNotFoundException extends RuntimeException{
    public TradeNotFoundException(UUID id){
        super("Trade with id " + id + " not found");
    }
}
