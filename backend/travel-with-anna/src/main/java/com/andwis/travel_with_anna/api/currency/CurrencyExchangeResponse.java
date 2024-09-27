package com.andwis.travel_with_anna.api.currency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CurrencyExchangeResponse implements Comparable<CurrencyExchangeResponse> {
    private String code;
    private BigDecimal value;

    @Override
    public int compareTo(CurrencyExchangeResponse obj) {
        return this.code.compareTo(obj.code);
    }
}