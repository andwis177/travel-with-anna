package com.andwis.travel_with_anna.api.country;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
public class CountryCurrency implements Comparable<CountryCurrency> {
    private String country;
    private String currency;

    @Override
    public int compareTo(@NotNull CountryCurrency obj) {
        return this.currency.compareTo(obj.currency);
    }
}
