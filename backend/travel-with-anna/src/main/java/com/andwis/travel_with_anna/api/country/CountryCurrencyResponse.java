package com.andwis.travel_with_anna.api.country;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CountryCurrencyResponse {
    private boolean error;
    private String msg;
    private List<CountryCurrency> data;
}
