package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryClient countryClient;

    @GetMapping("/names-all")
    public ResponseEntity<List<CountryName>> findAllCountryNames() {
        List<CountryName> countryNames = countryClient.fetchAllCountryNames();
        return ResponseEntity.ok(countryNames);
    }

    @GetMapping("/currencies-all")
    public ResponseEntity<List<CountryCurrency>> findAllCountryCurrencies() {
        List<CountryCurrency> currencies = countryClient.fetchAllCurrencies();
        return  ResponseEntity.ok(currencies);
    }
}
