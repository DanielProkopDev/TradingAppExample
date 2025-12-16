package com.example.tradingappexample.controller;


import com.example.tradingappexample.dto.*;
import com.example.tradingappexample.service.OrderService;
import com.example.tradingappexample.service.TradeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService service;
    private final TradeService tradeService;
    public OrderController(OrderService service, TradeService tradeService)
    {
        this.service = service;
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader("Idempotency-Key") @NotBlank String idemKey,
            @Valid @RequestBody CreateOrderRequest request
    ){
        OrderResult result = service.create(request,idemKey);

        if(result.replayed()){
            return ResponseEntity.ok()
                    .header("Idempotent-Replay","true")
                    .body(result.order());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.order());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping("/{id}/fill")
    public ResponseEntity<OrderResponse> fill(
            @PathVariable UUID id,
            @Valid @RequestBody FillOrderRequest request
    ){
        return ResponseEntity.ok(service.fill(id,request.quantity()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id){
        return ResponseEntity.ok(service.cancel(id));
    }
    @GetMapping("/{id}/trades")
    public ResponseEntity<List<TradeResponse>> getTrades(@PathVariable UUID id){
     return ResponseEntity.ok(tradeService.getTradesForOrder(id));
    }



}
