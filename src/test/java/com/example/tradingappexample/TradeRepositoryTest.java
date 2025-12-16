package com.example.tradingappexample;


import com.example.tradingappexample.dao.Side;
import com.example.tradingappexample.dao.Trade;
import com.example.tradingappexample.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TradeRepositoryTest {
    @Autowired
    TradeRepository repo;

    @Test
    void saveAndFind(){
        Trade t = repo.save(new Trade("EURUSD", Side.BUY,new BigDecimal("1"),new BigDecimal("1.1"),null));
        assertTrue(repo.findById(t.getId()).isPresent());
    }
}
