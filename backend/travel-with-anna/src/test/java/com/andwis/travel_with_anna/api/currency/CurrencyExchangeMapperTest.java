package com.andwis.travel_with_anna.api.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyExchangeMapperTest {
    @Test
    void testMapToCurrencyExchange_ShouldMapCorrectly() {
        // Given
        CurrencyExchangeResponse response = CurrencyExchangeResponse.builder()
                .code("USD")
                .value(BigDecimal.valueOf(1.2345678912345))
                .build();

        // When
        CurrencyExchange currencyExchange = CurrencyExchangeMapper.mapToCurrencyExchange(response);

        // Then
        assertThat(currencyExchange.getCode()).isEqualTo("USD");
        assertThat(currencyExchange.getExchangeValue()).isEqualByComparingTo(BigDecimal.valueOf(1.234567891235));
        assertThat(currencyExchange.getTimeStamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}