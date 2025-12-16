package com.example.tradingappexample.exceptions;

import java.math.BigDecimal;

public class InvalidFillQuantityException extends OrderDomainException{
    public InvalidFillQuantityException(BigDecimal qty) {
        super("Fill quantity is invalid: " + qty);
    }
}
