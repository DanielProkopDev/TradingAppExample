package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class TradeNotFoundException extends RuntimeException{
    public TradeNotFoundException(UUID id){
        super("Trade with id " + id + " not found");
    }
}
