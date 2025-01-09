package com.andwis.travel_with_anna.api.currency;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class CurrencyExchangeMapper {

    public static CurrencyExchange mapToCurrencyExchange(@NotNull CurrencyExchangeResponse response) {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        return CurrencyExchange.builder()
                .code(response.getCode())
                .exchangeValue(response.getValue())
                .timestamp(currentTimestamp)
                .build();
    }
}
