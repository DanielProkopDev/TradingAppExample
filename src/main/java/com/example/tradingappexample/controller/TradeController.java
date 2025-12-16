package com.example.tradingappexample.controller;


import com.example.tradingappexample.dto.CreateTradeRequest;
import com.example.tradingappexample.dto.TradeResponse;
import com.example.tradingappexample.dto.TradeResult;
import com.example.tradingappexample.dto.UpdateTraderPriceRequest;
import com.example.tradingappexample.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService service;

    public TradeController(TradeService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TradeResponse> create(@RequestHeader(name = "Idempotency-Key",required=false) String idemKey,
                                                @Valid @RequestBody CreateTradeRequest request){
        TradeResult result = service.create(request,idemKey);
        if(result.replayed()){
            return ResponseEntity
                    .ok()
                    .header("Idempotent-Replay","true")
                    .body(result.trade());

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.trade());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> get(@PathVariable UUID id){
        TradeResponse response = service.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
