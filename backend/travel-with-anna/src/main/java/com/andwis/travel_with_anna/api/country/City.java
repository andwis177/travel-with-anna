package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class City implements Comparable<City> {
    private static final String CITY_PROPERTY_NAME = "city";

    @JsonProperty(CITY_PROPERTY_NAME)
    @EqualsAndHashCode.Include
    private String name;

    public static final Comparator<City> NAME_COMPARATOR =
            Comparator.comparing(City::getName);

    @Override
    public int compareTo(@NotNull City o) {
        return NAME_COMPARATOR.compare(this, o);
    }
}
