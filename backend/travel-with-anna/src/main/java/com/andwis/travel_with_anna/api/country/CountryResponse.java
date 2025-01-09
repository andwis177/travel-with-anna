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
public class CountryResponse implements EntityResponse<Country> {

    private static final String DATA_JSON_PROPERTY = "data";

    @Builder.Default
    @JsonProperty(DATA_JSON_PROPERTY)
    private List<Country> countries = new ArrayList<>();

    @Override
    public List<Country> getEntities() {
        return countries;
    }
}
