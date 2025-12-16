package com.example.tradingappexample.service;


import com.example.tradingappexample.dto.UpdateTraderPriceRequest;
import com.example.tradingappexample.exceptions.TradeNotFoundException;
import com.example.tradingappexample.dao.IdempotencyKey;
import com.example.tradingappexample.dao.Trade;
import com.example.tradingappexample.dto.CreateTradeRequest;
import com.example.tradingappexample.dto.TradeResponse;
import com.example.tradingappexample.dto.TradeResult;
import com.example.tradingappexample.exceptions.TradeVersionMismatchException;
import com.example.tradingappexample.repository.IdempotencyKeyRepository;
import com.example.tradingappexample.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(idemKey != null && !idemKey.isBlank()) {
            var existing = idempotency.findByIdemKey(idemKey);
            if(existing.isPresent()){
                Trade trade = existing.get().getTrade();
                return new TradeResult(toResponse(trade), true);
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
        return new  TradeResult(toResponse(saved), false);
    }

    @Transactional(readOnly = true)
    public TradeResponse get(UUID id){
        Trade t = trades.findById(id).orElseThrow(()->new TradeNotFoundException(id));
        return toResponse(t);
    }





    private static TradeResponse toResponse(Trade t){
        return new TradeResponse(t.getId(),t.getVersion(),t.getSymbol(),t.getSide(),t.getQuantity(),t.getPrice(),t.getCreatedAt());
    }
}
