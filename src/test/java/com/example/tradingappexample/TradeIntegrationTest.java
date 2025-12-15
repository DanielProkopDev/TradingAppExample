package com.example.tradingappexample;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createTrade_isIdempotent() throws Exception {
        String requestBody = """
            {
              "symbol": "EURUSD",
              "side": "BUY",
              "quantity": 1000,
              "price": 1.095
            }
            """;
        String response1 = mockMvc.perform(post("/api/v1/trades")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/trades")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(header().string("Idempotent-Replay", "true"))
                .andExpect(jsonPath("$.id").value(
                        objectMapper.readTree(response1).get("id").asText()
                ));
    }


    @Test
    void updatePrice_withStaleVersion_returns409() throws Exception {

        String createBody = """
        {"symbol":"EURUSD","side":"BUY","quantity":1000,"price":1.095}
    """;

        String created = mockMvc.perform(post("/api/v1/trades")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("trader", "password"))
                        .header("Idempotency-Key", "idem-ver-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.version").exists())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(created).get("id").asText();


        String patchBody = """
        {"version": 999, "price": 1.200}
    """;

        mockMvc.perform(patch("/api/v1/trades/" + id + "/price")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("trader", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(anyOf(
                        is("VERSION_MISMATCH"),
                        is("OPTIMISTIC_LOCK_CONFLICT")
                )));
    }
}
