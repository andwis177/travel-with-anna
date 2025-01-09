package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Comparable<Country> {

        private static final String NAME_PROPERTY = "name";
        private static final String CURRENCY_PROPERTY = "currency";
        private static final String ISO2_PROPERTY = "iso2";
        private static final String ISO3_PROPERTY = "iso3";

        @JsonProperty(NAME_PROPERTY)
        @EqualsAndHashCode.Include
        private String name;

        @JsonProperty(CURRENCY_PROPERTY)
        private String currency;

        @JsonProperty(ISO2_PROPERTY)
        @EqualsAndHashCode.Include
        private String iso2;

        @JsonProperty(ISO3_PROPERTY)
        @EqualsAndHashCode.Include
        private String iso3;

        @Override
        public int compareTo(@NotNull Country o) {
                return this.name.compareTo(o.name);
        }
}