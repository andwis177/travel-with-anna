package com.andwis.travel_with_anna.api.country;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CountryCurrency implements Comparable<CountryCurrency> {
    @EqualsAndHashCode.Include
    private String country;

    @EqualsAndHashCode.Include
    private String currency;

    @Override
    public int compareTo(@NotNull CountryCurrency o) {
        return this.currency.compareTo(o.currency);
    }
}
