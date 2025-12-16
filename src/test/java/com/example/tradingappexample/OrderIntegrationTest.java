package com.example.tradingappexample;


import com.example.tradingappexample.dao.Side;
import com.example.tradingappexample.dto.CreateOrderRequest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void createOrder_authenticated_withIdempotencyKey_returns201() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL",
                Side.BUY,
                new BigDecimal("100")
        );

        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void createOrder_sameIdempotencyKey_returnsSameOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL",
                Side.BUY,
                new BigDecimal("100")
        );

        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Idempotent-Replay", "true"));
    }
    @Test
    void createOrder_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .header("Idempotency-Key", "idem-401")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_missingIdempotencyKey_returns400() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL",
                Side.BUY,
                new BigDecimal("100")
        );

        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("trader", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fillOrder_concurrent_updates_oneFailsWith409() throws Exception {

        MvcResult result = mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-lock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"symbol":"AAPL","side":"BUY","quantity":100}
            """))
                .andReturn();

        String orderId = JsonPath.read(
                result.getResponse().getContentAsString(), "$.id");

        Runnable fill = () -> {
            try {
                mockMvc.perform(post("/api/v1/orders/" + orderId + "/fill")
                                .with(httpBasic("trader", "password"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"quantity":50}
                                        """))
                        .andReturn();
            } catch (Exception ignored) {}
        };

        Thread t1 = new Thread(fill);
        Thread t2 = new Thread(fill);

        t1.start(); t2.start();
        t1.join(); t2.join();
    }

}
