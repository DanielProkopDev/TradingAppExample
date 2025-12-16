package com.example.tradingappexample.dao;


import com.example.tradingappexample.exceptions.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name ="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false,updatable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false)
    private Side side;

    @Column(nullable = false, precision=19,scale=6,updatable=false)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale=6)
    private BigDecimal filledQuantity =  BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    protected Order() {}

    public Order(String symbol, Side side, BigDecimal quantity) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
    }

    public void fill(BigDecimal qty){
        if(qty.compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidFillQuantityException(qty);
        }
        if(this.status != OrderStatus.NEW && this.status != OrderStatus.PARTIALLY_FILLED){
            throw new OrderAlreadyClosedException(this.id);
        }
        BigDecimal newFilled = this.filledQuantity.add(qty);

        if(newFilled.compareTo(this.quantity)>0){
            throw new OrderOverfillException(newFilled,this.quantity);
        }

        this.filledQuantity = newFilled;
        if(newFilled.compareTo(this.quantity)==0){
            this.status = OrderStatus.FILLED;
        }else{
            this.status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel(){
        if(this.status == OrderStatus.FILLED){
            throw new OrderAlreadyFilledException(this.id);
        }
        if(this.status == OrderStatus.CANCELLED){
            throw new OrderAlreadyCancelledException(this.id);
        }
        this.status= OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
