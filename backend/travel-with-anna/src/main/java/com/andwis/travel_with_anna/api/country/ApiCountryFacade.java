package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiCountryFacade {

    private final CountryService countryService;

    public List<Country> fetchAllCountryNames() {
        return countryService.fetchAllCountries();
    }

    public List<City> fetchAllCountryCities(String countryName) {
        return countryService.fetchAllCountryCities(countryName);
    }

    public List<CountryCurrency> fetchAllCountriesCurrencies() {
        return countryService.fetchAllCountriesCurrencies();
    }
}
