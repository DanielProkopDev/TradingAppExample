package com.example.tradingappexample.service;


import com.example.tradingappexample.dao.Order;
import com.example.tradingappexample.dao.OrderIdempotencyKey;
import com.example.tradingappexample.dto.CreateOrderRequest;
import com.example.tradingappexample.dto.OrderResponse;
import com.example.tradingappexample.dto.OrderResult;
import com.example.tradingappexample.exceptions.OrderNotFoundException;
import com.example.tradingappexample.repository.OrderIdempotencyKeyRepository;
import com.example.tradingappexample.repository.OrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

//i can imagine that client would have to generate idempotency key each time user will open new trade/order, if
//i let creation of entities into db with null idempotency key that opens the door for loads of unwanted behavior
//contract is simple, client wants to process order or trade, it has to generate new idemkey(how? i leave it to client side)
// no idemkey no trade no order, mainly its a protection from repeats on the same request, identical orders and trades are allowed
//only without duplicate idemkey
@Service
@Transactional
public class OrderService {

    private final OrderRepository orders;
    private final OrderIdempotencyKeyRepository idempotency;
    private final TradeService tradeService;
    public OrderService(OrderRepository orderRepository,  OrderIdempotencyKeyRepository idempotency,  TradeService tradeService) {
        this.orders = orderRepository;
        this.idempotency = idempotency;
        this.tradeService = tradeService;

    }


    public OrderResult create(CreateOrderRequest req, String idemKey) {
        //test
        var existing = idempotency.findByIdemKey(idemKey);
        if (existing.isPresent()) {
            return new OrderResult(
                    OrderResponse.from(existing.get().getOrder()),
                    true
            );
        }
        //race
        try {
            Order order = orders.save(new Order(
                    req.symbol().trim().toUpperCase(),
                    req.side(),
                    req.quantity()
            ));

            idempotency.save(new OrderIdempotencyKey(idemKey, order));


            return new OrderResult(
                    OrderResponse.from(order),
                    false
            );

        } catch (DataIntegrityViolationException e) {

            Order replayed = idempotency.findByIdemKey(idemKey)
                    .orElseThrow()
                    .getOrder();

            return new OrderResult(
                    OrderResponse.from(replayed),
                    true
            );
        }
    }

    public OrderResponse get(UUID id){
        return OrderResponse.from(find(id));
    }

    public OrderResponse fill(UUID id, BigDecimal qty){
        Order order=find(id);

        order.fill(qty);
        orders.saveAndFlush(order);
        tradeService.createFromOrder(order,qty,currentMarketPrice(order.getSymbol()));

        return OrderResponse.from(order);
    }

    public OrderResponse cancel(UUID id){
        Order order=find(id);

        order.cancel();
        orders.saveAndFlush(order);

        return OrderResponse.from(order);
    }


    private Order find(UUID id){
        return orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private BigDecimal currentMarketPrice(String symbol) {
        return BigDecimal.valueOf(100); // stub, it would be taken from market data in rl
    }


}
