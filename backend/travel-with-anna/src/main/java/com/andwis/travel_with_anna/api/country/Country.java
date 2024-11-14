package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Comparable<Country> {
        @JsonProperty("name")
        private String name;
        @JsonProperty("currency")
        private String currency;
        @JsonProperty("iso2")
        private String iso2;
        @JsonProperty("iso3")
        private String iso3;

        @Override
        public int compareTo(@NotNull Country obj) {
                return this.name.compareTo(obj.name);
        }

        @Override
        public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                Country country = (Country) object;
                return Objects.equals(name, country.name) && Objects.equals(iso2, country.iso2) && Objects.equals(iso3, country.iso3);
        }

        @Override
        public int hashCode() {
                return Objects.hash(name, iso2, iso3);
        }
}
