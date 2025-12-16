package com.example.tradingappexample.dao;


import jakarta.persistence.*;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Table(name="trades")
public class Trade {
    @Id @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable=false, updatable = false, length=16)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false, length=4)
    private Side side;

    @Column(nullable = false,updatable = false, precision=19, scale=6)
    private BigDecimal quantity;

    @Column(nullable=false,updatable = false, precision = 19, scale=6)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    protected Trade() {}

    public Trade(String symbol, Side side, BigDecimal quantity, BigDecimal price, Order order) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = Instant.now();
        this.order = order;
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

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
