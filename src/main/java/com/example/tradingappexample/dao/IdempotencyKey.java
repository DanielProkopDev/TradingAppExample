package com.example.tradingappexample.dao;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="idempotency_keys", uniqueConstraints = @UniqueConstraint(columnNames = "idemKey"))
public class IdempotencyKey{
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length=80)
    private String idemKey;

    @OneToOne(optional = false, fetch= FetchType.LAZY)
    @JoinColumn(
            name="trade_id",
            nullable = false,
            foreignKey = @ForeignKey(name="fk_idempotency_trade")
    )
    private Trade trade;

    @Column(nullable = false)
    private Instant createdAt;

    protected IdempotencyKey() {}

    public IdempotencyKey(String idemKey, Trade trade) {
        this.idemKey = idemKey;
        this.trade = trade;
        this.createdAt = Instant.now();
    }

    public Trade getTrade() {
        return trade;
    }
}
