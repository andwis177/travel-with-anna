package com.andwis.travel_with_anna.api.country;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CountryClient {
    private final RestClient restClient;
    @Value("${my_apis.country.country_url}")
    private String baseUrl;

    public List<CountryName> fetchAllCountryNames() {
        CountryNameResponse response = restClient.get()
                .uri(baseUrl + "/api/v0.1/countries/currency")
                .retrieve()
                .body(CountryNameResponse.class);

        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }

        HashSet<CountryName> countryNames = new HashSet<>(response.getData());
        List<CountryName> countryNamesList = new ArrayList<>(countryNames);
        countryNamesList.sort(Comparator.comparing(CountryName::getName));
        return countryNamesList;
    }

    public List<CountryCurrency> fetchAllCurrencies() {
        CountryCurrencyResponse response = restClient.get()
                .uri(baseUrl + "/api/v0.1/countries/currency")
                .retrieve()
                .body(CountryCurrencyResponse.class);
        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }
        HashSet<CountryCurrency> countryCurrencies = new HashSet<>(response.getData());
        List<CountryCurrency> countryCurrenciesList = new ArrayList<>(countryCurrencies);
        countryCurrenciesList.sort(Comparator.comparing(CountryCurrency::getCurrency));

        if(countryCurrenciesList.getFirst().getCurrency().isEmpty()) {
            countryCurrenciesList.removeFirst();
        }
        return countryCurrenciesList;
    }
}
