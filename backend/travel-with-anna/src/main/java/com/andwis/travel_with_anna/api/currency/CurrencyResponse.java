package com.andwis.travel_with_anna.api.currency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class CurrencyResponse {
    private MetaDto meta;
    private Map<String, CurrencyExchangeResponse> data;
}
