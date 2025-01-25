package com.andwis.travel_with_anna.api.currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest
@DisplayName("Currency Exchange Client tests")
class CurrencyExchangeClientTest {
    @Autowired
    private CurrencyExchangeClient currencyExchangeClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Test
    void testFetchAllCountries(){
        // Given
        // When
        List<CurrencyExchangeResponse> currencies = currencyExchangeClient.fetchAllExchangeRates();

        // Then
        Assertions.assertFalse(currencies.isEmpty());
        List<String> currenciesNames = currencies.stream().map(CurrencyExchangeResponse::getCode).toList();
        Assertions.assertTrue( currenciesNames.contains("USD"));
        Assertions.assertTrue( currenciesNames.contains("PLN"));
        Assertions.assertTrue( currenciesNames.contains("EUR"));
        Assertions.assertTrue( currenciesNames.contains("CHF"));
    }
}