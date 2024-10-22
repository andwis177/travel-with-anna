package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.api.country.City;
import com.andwis.travel_with_anna.api.country.Country;

import java.util.List;

public record AddressDetail(
        List<Country> countries,
        Country lastCountry,
        List<City> cities,
        City lastCity
) {
}
