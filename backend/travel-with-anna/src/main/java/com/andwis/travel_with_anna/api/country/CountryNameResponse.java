package com.andwis.travel_with_anna.api.country;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CountryNameResponse {
    private List<CountryName> data;
}
