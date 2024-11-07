package com.andwis.travel_with_anna.api.country;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
@Builder
@Jacksonized
public class City implements Comparable<City> {
    private String city;

    @Override
    public int compareTo(@NotNull City obj) {
        return this.city.compareTo(obj.city);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        City city1 = (City) object;
        return Objects.equals(city, city1.city);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(city);
    }
}
