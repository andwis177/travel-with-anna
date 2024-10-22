package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryClient countryClient;

    @GetMapping("/names-all")
    public ResponseEntity<List<Country>> findAllCountryNames() {
        List<Country> countryNames = countryClient.fetchAllCountries();
        return ResponseEntity.ok(countryNames);
    }

    @GetMapping("/currencies-all")
    public ResponseEntity<List<CountryCurrency>> findAllCountryCurrencies() {
        List<CountryCurrency> currencies = countryClient.fetchAllCountriesCurrencies();
        return  ResponseEntity.ok(currencies);
    }

    @PostMapping("/cities-all")
    public ResponseEntity<List<City>> findAllCountryCities(@RequestParam String country) {
        List<City> countryCities = countryClient.fetchAllCountryCities(country);
        return ResponseEntity.ok(countryCities);
    }
}
