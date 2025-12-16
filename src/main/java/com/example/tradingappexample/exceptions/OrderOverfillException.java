package com.example.tradingappexample.exceptions;

import java.math.BigDecimal;

public class OrderOverfillException extends OrderDomainException{
    public OrderOverfillException(BigDecimal attempted, BigDecimal total) {
        super("Overfill detected: attempted=" + attempted + "> total=" + total);
    }
}
