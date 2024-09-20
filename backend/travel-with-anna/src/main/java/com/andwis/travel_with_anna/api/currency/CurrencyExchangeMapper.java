package com.andwis.travel_with_anna.api.currency;

import java.math.RoundingMode;
import java.time.LocalDateTime;

public class CurrencyExchangeMapper {

    public static CurrencyExchange mapToCurrencyExchange(CurrencyExchangeResponse currencyExchangeResponse) {
        return CurrencyExchange.builder()
                .code(currencyExchangeResponse.getCode())
                .exchangeValue(currencyExchangeResponse.getValue().setScale(12, RoundingMode.HALF_UP))
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
