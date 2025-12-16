package com.example.tradingappexample.exceptions;

public abstract class OrderDomainException extends RuntimeException{
    protected OrderDomainException(String message){
        super(message);
    }
}
