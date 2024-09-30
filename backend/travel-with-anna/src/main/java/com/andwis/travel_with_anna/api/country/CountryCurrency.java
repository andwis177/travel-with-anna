package com.andwis.travel_with_anna.api.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryCurrency implements Comparable<CountryCurrency> {
    private String currency;

    @Override
    public int compareTo(@NotNull CountryCurrency obj) {
        return this.currency.compareTo(obj.currency);
    }
}
