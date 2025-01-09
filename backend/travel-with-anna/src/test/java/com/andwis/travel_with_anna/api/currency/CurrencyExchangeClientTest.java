package com.andwis.travel_with_anna.api.currency;

import com.andwis.travel_with_anna.api.client.RestClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CurrencyExchangeClient.class)
@Import(RestClientConfig.class)
@DisplayName("Currency Exchange Client tests")
class CurrencyExchangeClientTest {
    @Autowired
    MockRestServiceServer server;
    @Autowired
    CurrencyExchangeClient currencyExchangeService;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${my_apis.currency.currency_url}")
    private String baseUrl;
    @Value("${my_apis.currency.header.apikey}")
    private String apiKey;

    @Test
    void testGetAllExchangeRates() throws JsonProcessingException {
        // Given:
        Map<String, CurrencyExchangeResponse> mockData = Map.of(
                "USD", CurrencyExchangeResponse.builder().code("USD").value(BigDecimal.valueOf(1.17)).build(),
                "EUR", CurrencyExchangeResponse.builder().code("EUR").value(BigDecimal.valueOf(2.87)).build()
        );

        CurrencyResponse mockResponse = CurrencyResponse.builder().exchangeRates(mockData).build();

        // When:
        this.server.reset();
        this.server
                .expect(requestTo(baseUrl + "/latest"))
                .andExpect(header("apikey", apiKey))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        List<CurrencyExchangeResponse> exchangeRates = currencyExchangeService.fetchAllExchangeRates();

        // Then:
        assertThat(exchangeRates).hasSize(2);
        assertThat(exchangeRates.get(0).getCode()).isEqualTo("EUR");
        assertThat(exchangeRates.get(0).getValue()).isEqualTo(BigDecimal.valueOf(2.87));
        assertThat(exchangeRates.get(1).getCode()).isEqualTo("USD");
        assertThat(exchangeRates.get(1).getValue()).isEqualTo(BigDecimal.valueOf(1.17));
    }
}