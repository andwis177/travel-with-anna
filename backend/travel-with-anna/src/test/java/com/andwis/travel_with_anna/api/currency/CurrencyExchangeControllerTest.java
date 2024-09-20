package com.andwis.travel_with_anna.api.currency;

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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Currency Exchange Controller tests")
class CurrencyExchangeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyExchangeClient currencyExchangeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getCurrencyExchangeRates_ShouldReturnExchangeRates() throws Exception {
        // Given
        CurrencyExchangeResponse usdExchange = CurrencyExchangeResponse.builder()
                .code("USD")
                .value(BigDecimal.valueOf(1.17))
                .build();

        CurrencyExchangeResponse eurExchange = CurrencyExchangeResponse.builder()
                .code("EUR")
                .value(BigDecimal.valueOf(2.87))
                .build();

        List<CurrencyExchangeResponse> exchangeRates = new ArrayList<>();
        exchangeRates.add(usdExchange);
        exchangeRates.add(eurExchange);

        when(currencyExchangeClient.getAllExchangeRates()).thenReturn(exchangeRates);
        String jsonContent = objectMapper.writeValueAsString(exchangeRates);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/exchange/rates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }
}