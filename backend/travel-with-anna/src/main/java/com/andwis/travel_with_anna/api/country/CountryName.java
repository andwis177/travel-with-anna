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
public class CountryName implements Comparable<CountryName> {
        private String name;
        private String Iso2;
        private String Iso3;

        @Override
        public int compareTo(@NotNull CountryName obj) {
                return this.name.compareTo(obj.name);
        }
}
