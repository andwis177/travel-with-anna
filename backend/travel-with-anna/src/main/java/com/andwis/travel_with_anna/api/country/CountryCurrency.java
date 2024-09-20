package com.andwis.travel_with_anna.api.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryCurrency implements Comparable<CountryCurrency> {
    private String currency;

    @Override
    public int compareTo(CountryCurrency obj) {
        return this.currency.compareTo(obj.currency);
    }
}
