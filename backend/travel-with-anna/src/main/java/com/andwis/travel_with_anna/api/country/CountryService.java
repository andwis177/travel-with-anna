package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryClient countryClient;

    public List<Country> fetchAllCountries() {
        CountryResponse response = countryClient.getAllCountries();
        return deduplicateAndSort(
                Optional.ofNullable(response)
                        .map(CountryResponse::getCountries)
                        .orElse(Collections.emptyList()),
                Comparator.comparing(Country::getName)
        );
    }

    public List<City> fetchAllCountryCities(String countryName) {
        CountryCitiesResponse response = countryClient.getAllCountryCities(countryName);
        return deduplicateAndSort(
                Optional.ofNullable(response).map(CountryCitiesResponse::getCities).orElse(Collections.emptyList()),
                Comparator.comparing(City::getName)
        );
    }

    public List<CountryCurrency> fetchAllCountriesCurrencies() {

        return fetchAllCountries().stream()
                .map(country -> CountryCurrency.builder()
                        .country(country.getName())
                        .currency(country.getCurrency())
                        .build())
                .sorted(Comparator.comparing(CountryCurrency::getCurrency))
                .toList();
    }

    public <T> List<T> deduplicateAndSort(List<T> items, Comparator<T> comparator) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .distinct()
                .sorted(comparator)
                .toList();
    }
}
