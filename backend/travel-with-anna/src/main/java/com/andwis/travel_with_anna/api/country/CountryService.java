package com.andwis.travel_with_anna.api.country;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
public class CountryService {
    private final RestClient restClient;

    public CountryService() {
        restClient = RestClient.builder()
                .baseUrl("https://countriesnow.space")
                .build();
    }

    public CountryNameResponse findAllCountryNames() {
        return restClient.get()
                .uri("/api/v0.1/countries/iso")
                .retrieve()
                .body(CountryNameResponse.class);
    }

    public List<CountryCurrency> findAllCurrencies() {
        CountryCurrencyResponse response = restClient.get()
                .uri("/api/v0.1/countries/currency")
                .retrieve()
                .body(CountryCurrencyResponse.class);
        if (response == null) {
            return null;
        }
        HashSet<CountryCurrency> countryCurrencies = new HashSet<>(response.getData());
        List<CountryCurrency> countryCurrenciesList = new ArrayList<>(countryCurrencies);
        countryCurrenciesList.sort(Comparator.comparing(CountryCurrency::getCurrency));
        return countryCurrenciesList;
    }
}
