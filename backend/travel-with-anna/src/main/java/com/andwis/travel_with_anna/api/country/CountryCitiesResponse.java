package com.andwis.travel_with_anna.api.country;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CountryCitiesResponse {
    private boolean error;
    private String msg;
    private List<City> data;
}
