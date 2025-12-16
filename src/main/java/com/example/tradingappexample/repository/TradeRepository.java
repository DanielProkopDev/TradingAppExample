package com.example.tradingappexample.repository;

import com.example.tradingappexample.dao.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {
    List<Trade> findByOrderId(UUID orderId);
}
