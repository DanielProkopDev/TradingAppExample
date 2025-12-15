package com.example.tradingappexample.dao;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="trades")
public class Trade {
    @Id @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable=false, length=16)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=4)
    private Side side;

    @Column(nullable = false, precision=19, scale=6)
    private BigDecimal quantity;

    @Column(nullable=false, precision = 19, scale=6)
    private BigDecimal price;

    @Column(nullable = false)
    private Instant createdAt;

    protected Trade() {}

    public Trade(String symbol, Side side, BigDecimal quantity, BigDecimal price) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = Instant.now();
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
