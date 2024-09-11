package com.andwis.travel_with_anna.api.country;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class CountryService {
    private final RestClient restClient;

    public CountryService(@Value("${my_apis.country.country_url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
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
        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }
        HashSet<CountryCurrency> countryCurrencies = new HashSet<>(response.getData());
        List<CountryCurrency> countryCurrenciesList = new ArrayList<>(countryCurrencies);
        countryCurrenciesList.sort(Comparator.comparing(CountryCurrency::getCurrency));
        return countryCurrenciesList;
    }
}
