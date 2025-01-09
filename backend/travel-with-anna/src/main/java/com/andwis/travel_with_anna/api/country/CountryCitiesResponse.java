package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryCitiesResponse implements EntityResponse<City> {

    private static final String DATA_PROPERTY = "data";

    @Builder.Default
    @JsonProperty(DATA_PROPERTY)
    private List<City> cities = new ArrayList<>();

    @Override
    public List<City> getEntities() {
        return cities;
    }
}
