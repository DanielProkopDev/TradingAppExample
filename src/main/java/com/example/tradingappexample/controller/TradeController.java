package com.example.tradingappexample.controller;


import com.example.tradingappexample.dto.CreateTradeRequest;
import com.example.tradingappexample.dto.TradeResponse;
import com.example.tradingappexample.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService service;

    public TradeController(TradeService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradeResponse create(@RequestHeader(name = "Idempotency-Key",required=false) String idemKey,
                                @Valid @RequestBody CreateTradeRequest request){
        return service.create(request,idemKey);
    }

    @GetMapping("/{id}")
    public TradeResponse get(@PathVariable UUID id){
        return service.get(id);
    }
}
