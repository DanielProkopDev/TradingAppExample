package com.example.tradingappexample.repository;

import com.example.tradingappexample.dao.OrderIdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderIdempotencyKeyRepository extends JpaRepository<OrderIdempotencyKey, UUID> {
    Optional<OrderIdempotencyKey> findByIdemKey(String idemKey);
}
