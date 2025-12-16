package com.example.tradingappexample.dao;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name="order_idempotency_keys",
        uniqueConstraints = @UniqueConstraint(name="uk_orderidempotency_idem_key",columnNames = "idem_Key")
)
public class OrderIdempotencyKey {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=80)
    private String idemKey;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected OrderIdempotencyKey() {}

    public OrderIdempotencyKey(String idemKey, Order order) {
        this.idemKey = idemKey;
        this.order = order;
    }

    public String getIdemKey() {
        return idemKey;
    }

    public Order getOrder() {
        return order;
    }
}
