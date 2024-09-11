package com.andwis.travel_with_anna.api.country;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryCurrency implements Comparable<CountryCurrency> {
    private String currency;

    @Override
    public int compareTo(CountryCurrency other) {
        return this.currency.compareTo(other.currency);
    }
}
