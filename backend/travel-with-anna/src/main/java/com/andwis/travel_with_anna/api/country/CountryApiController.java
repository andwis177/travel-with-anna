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
public class CountryApiController {

    private final CountryService countryService;

    @GetMapping("/names-all")
    public CountryNameResponse findAllCountryNames() {
        return countryService.findAllCountryNames();
    }

    @GetMapping("/currencies-all")
    public ResponseEntity<List<CountryCurrency>> findAllCountryCurrencies() {
        List<CountryCurrency> currencies = countryService.findAllCurrencies();
        return  ResponseEntity.ok(currencies);
    }
}
