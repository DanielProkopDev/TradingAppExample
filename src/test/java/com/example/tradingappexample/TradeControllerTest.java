package com.example.tradingappexample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser(roles = "TRADER")
    void createTrade_returns201() throws Exception {
        mvc.perform(post("/api/v1/trades")
                .contentType("application/json")
                .content("""
                        {"symbol":"EURUSD","side":"BUY","quantity":1000,"price":1.095}
                        """)).andExpect(status().isCreated());

    }
}
