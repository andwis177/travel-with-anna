package com.andwis.travel_with_anna.api.currency;

import java.math.RoundingMode;
import java.time.LocalDateTime;

public class CurrencyExchangeMapper {

    public static CurrencyExchange mapToCurrencyExchange(CurrencyExchangeDto currencyExchangeDto) {
        return CurrencyExchange.builder()
                .code(currencyExchangeDto.getCode())
                .exchangeValue(currencyExchangeDto.getValue().setScale(12, RoundingMode.HALF_UP))
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
