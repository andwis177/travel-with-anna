package com.andwis.travel_with_anna.api.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryCurrencyResponse {
    private boolean error;
    private String msg;
    private List<CountryCurrency> data;
}
