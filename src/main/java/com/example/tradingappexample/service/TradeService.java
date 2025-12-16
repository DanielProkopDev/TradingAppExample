package com.example.tradingappexample.service;


import com.example.tradingappexample.dao.Order;
import com.example.tradingappexample.dto.*;
import com.example.tradingappexample.exceptions.TradeNotFoundException;
import com.example.tradingappexample.dao.IdempotencyKey;
import com.example.tradingappexample.dao.Trade;
import com.example.tradingappexample.exceptions.TradeVersionMismatchException;
import com.example.tradingappexample.repository.IdempotencyKeyRepository;
import com.example.tradingappexample.repository.TradeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TradeService {

    private final TradeRepository trades;
    private final IdempotencyKeyRepository idempotency;
    public TradeService(TradeRepository trades, IdempotencyKeyRepository idempotency) {
        this.trades = trades;
        this.idempotency = idempotency;
    }

    public TradeResult create(CreateTradeRequest req, String idemKey) {

        var existing = idempotency.findByIdemKey(idemKey);
        if (existing.isPresent()) {
            return new TradeResult(
                    toResponse(existing.get().getTrade()),
                    true
            );
        }

        try {
            Trade trade = trades.save(new Trade(
                    req.symbol().trim().toUpperCase(),
                    req.side(),
                    req.quantity(),
                    req.price(),
                    null
            ));

            idempotency.save(new IdempotencyKey(idemKey, trade));

            return new TradeResult(
                    toResponse(trade),
                    false
            );
        }catch(DataIntegrityViolationException e){
            Trade replayed = idempotency.findByIdemKey(idemKey)
                    .orElseThrow()
                    .getTrade();

            return new TradeResult(
                    toResponse(replayed),
                    true
            );
        }
    }

    @Transactional(readOnly = true)
    public TradeResponse get(UUID id){
        Trade t = trades.findById(id).orElseThrow(()->new TradeNotFoundException(id));
        return toResponse(t);
    }
    @Transactional(readOnly = true)
    public List<TradeResponse> getTradesForOrder(UUID orderId) {
        return trades.findByOrderId(orderId).stream()
                .map(TradeService::toResponse)
                .toList();
    }

    public TradeResponse createFromOrder(Order order, BigDecimal qty, BigDecimal price){
        Trade trade = new Trade(
                order.getSymbol(),
                order.getSide(),
                qty,
                price,
                order
        );
        return toResponse(trades.save(trade));
    }


    private static TradeResponse toResponse(Trade t){
        return new TradeResponse(t.getId(),t.getVersion(),t.getSymbol(),t.getSide(),t.getQuantity(),t.getPrice(),t.getCreatedAt());
    }
}
