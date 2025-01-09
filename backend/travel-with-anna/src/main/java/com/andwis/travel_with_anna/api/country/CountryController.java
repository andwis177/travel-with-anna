package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "api/country")
@RequiredArgsConstructor
public class CountryController {

    private final ApiCountryFacade facade;

    @GetMapping("/names")
    public ResponseEntity<List<Country>> getAllCountryNames() {
        return ResponseEntity.ok(facade.fetchAllCountryNames());
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<CountryCurrency>> getAllCountryCurrencies() {
        return ResponseEntity.ok(facade.fetchAllCountriesCurrencies());
    }

    @PostMapping("/cities")
    public ResponseEntity<List<City>> getCountryCities(@RequestParam String countryName) {
        return ResponseEntity.ok(facade.fetchAllCountryCities(countryName));
    }
}
