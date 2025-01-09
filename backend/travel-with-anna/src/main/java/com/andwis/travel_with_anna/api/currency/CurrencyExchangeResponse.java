package com.andwis.travel_with_anna.api.currency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CurrencyExchangeResponse implements Comparable<CurrencyExchangeResponse> {

    private String code;
    private BigDecimal value;

    @Override
    public int compareTo(@NotNull CurrencyExchangeResponse o) {
        return this.code.compareTo(o.code);
    }
}
