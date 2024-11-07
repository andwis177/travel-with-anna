package com.andwis.travel_with_anna.trip.expanse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Expanse Controller Tests")
class ExpanseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExpanseFacade expanseFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testCreateOrUpdateExpanse_ShouldReturnCreatedAndExpanseResponse() throws Exception {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .expanseName("Hotel Booking")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(80.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120.00))
                .paidInTripCurrency(BigDecimal.valueOf(96.00))
                .build();

        ExpanseResponse response = new ExpanseResponse(
                1L,
                "Hotel Booking",
                "USD",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(120.00),
                BigDecimal.valueOf(96.00)
        );

        String requestBody = objectMapper.writeValueAsString(request);
        String jsonResponse = objectMapper.writeValueAsString(response);
        when(expanseFacade.createOrUpdateExpanse(any(ExpanseRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/expanse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExpanseById_ShouldReturnExpanseResponse() throws Exception {
        // Given
        Long expanseId = 1L;
        ExpanseResponse response = new ExpanseResponse(
                1L,
                "Hotel Booking",
                "USD",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(120.00),
                BigDecimal.valueOf(96.00)
        );
        when(expanseFacade.getExpanseById(expanseId)).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/{expanseId}", expanseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExpanseByEntity_ShouldReturnExpanseResponse() throws Exception {
        // Given
        Long entityId = 1L;
        String entityType = "ITEM";
        ExpanseResponse response = new ExpanseResponse(
                1L,
                "Hotel Booking",
                "USD",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(120.00),
                BigDecimal.valueOf(96.00)
        );
        when(expanseFacade.getExpanseByEntityId(entityId, entityType)).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/{entityId}/type/{entityType}", entityId, entityType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExchangeRate_ShouldReturnExchangeRate() throws Exception {
        // Given
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        ExchangeResponse exchangeResponse = new ExchangeResponse("", BigDecimal.valueOf(1.18));
        when(expanseFacade.getExchangeRate(currencyFrom, currencyTo)).thenReturn(exchangeResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/exchange")
                        .param("currencyFrom", currencyFrom)
                        .param("currencyTo", currencyTo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(exchangeResponse)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetTripCurrencyValues_ShouldReturnExpanseInTripCurrency() throws Exception {
        // Given
        BigDecimal price = BigDecimal.valueOf(500.00);
        BigDecimal paid = BigDecimal.valueOf(400.00);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.20);
        ExpanseInTripCurrency expanseInTripCurrency = new ExpanseInTripCurrency(
                BigDecimal.valueOf(600.00),
                BigDecimal.valueOf(480.00)
        );
        when(expanseFacade.getExpanseInTripCurrency(price, paid, exchangeRate)).thenReturn(expanseInTripCurrency);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/trip-currency-values")
                        .param("price", price.toString())
                        .param("paid", paid.toString())
                        .param("exchangeRate", exchangeRate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expanseInTripCurrency)));
    }
}