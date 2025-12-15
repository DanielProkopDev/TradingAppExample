package com.example.tradingappexample.exceptions;

import java.util.UUID;

public class TradeVersionMismatchException extends RuntimeException {
    public TradeVersionMismatchException(UUID id, long provided, long current) {
        super("Version conflict for trade " + id + ": provided=" + provided + ", current=" + current);
    }
}
