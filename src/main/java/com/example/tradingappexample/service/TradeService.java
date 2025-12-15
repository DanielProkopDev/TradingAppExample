package com.example.tradingappexample.service;


import com.example.tradingappexample.Exceptions.TradeNotFoundException;
import com.example.tradingappexample.dao.IdempotencyKey;
import com.example.tradingappexample.dao.Trade;
import com.example.tradingappexample.dto.CreateTradeRequest;
import com.example.tradingappexample.dto.TradeResponse;
import com.example.tradingappexample.repository.IdempotencyKeyRepository;
import com.example.tradingappexample.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TradeResponse create(CreateTradeRequest req, String idemKey) {
        if(idemKey != null && !idemKey.isBlank()) {
            var existing = idempotency.findByIdemKey(idemKey);
            if(existing.isPresent()){
                return toResponse(existing.get().getTrade());
            }
        }
        Trade saved = trades.save(new Trade(
           req.symbol().trim().toUpperCase(),
           req.side(),
           req.quantity(),
           req.price()
        ));

        if(idemKey != null && !idemKey.isBlank()) {
            idempotency.save(new IdempotencyKey(idemKey,saved));
        }
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TradeResponse get(UUID id){
        Trade t = trades.findById(id).orElseThrow(()->new TradeNotFoundException(id));
        return toResponse(t);
    }
    private static TradeResponse toResponse(Trade t){
        return new TradeResponse(t.getId(),t.getSymbol(),t.getSide(),t.getQuantity(),t.getPrice(),t.getCreatedAt());
    }
}
