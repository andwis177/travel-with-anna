package com.andwis.travel_with_anna.api.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyResponse {

    public static final String JSON_PROPERTY_DATA = "data";

    @JsonProperty(JSON_PROPERTY_DATA)
    private Map<String, CurrencyExchangeResponse> exchangeRates;
}
