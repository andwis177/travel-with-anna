package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryClient {
    private final RestClient restClient;
    @Value("${my_apis.country.country_url}")
    private String baseUrl;

    public List<Country> fetchAllCountries() {
        try {
            CountryResponse response = restClient.get()
                    .uri(baseUrl + "/api/v0.1/countries/currency")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(CountryResponse.class);
            return sortCountryNames(response);
        } catch (Exception e) {
            log.error("Error during fetching country names API call: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @NotNull
    private List<Country> sortCountryNames(CountryResponse response) {
        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }
        HashSet<Country> countryNames = new HashSet<>(response.getData());
        List<Country> countryNamesList = new ArrayList<>(countryNames);
        countryNamesList.sort(Comparator.comparing(Country::getName));
        return countryNamesList;
    }

    public List<City> fetchAllCountryCities(String country) {
        try {
            CountryCitiesResponse response = restClient.get()
                    .uri(baseUrl + "/api/v0.1/countries/cities/q?country=" + country.toLowerCase())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(CountryCitiesResponse.class);
            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }
            return sortCityNames(response);

        } catch (Exception e) {
            log.error("Error during fetching cities API call: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @NotNull
    private List<City> sortCityNames(CountryCitiesResponse response) {
        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }
        HashSet<City> cities = new HashSet<>(response.getData());
        List<City> citiesList = new ArrayList<>(cities);
        citiesList.sort(Comparator.comparing(City::getCity));
        return citiesList;
    }

    public List<CountryCurrency> fetchAllCountriesCurrencies() {
        List<Country> countries = fetchAllCountries();
        List<CountryCurrency> currencies = new ArrayList<>(countries.stream()
                .map(country -> CountryCurrency.builder()
                        .country(country.getName())
                        .currency(country.getCurrency())
                        .build())
                .toList());

        currencies.sort(Comparator.comparing(CountryCurrency::getCurrency));
        return currencies;
    }
}
