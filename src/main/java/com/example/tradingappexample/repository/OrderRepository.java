package com.example.tradingappexample.repository;

import com.example.tradingappexample.dao.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
