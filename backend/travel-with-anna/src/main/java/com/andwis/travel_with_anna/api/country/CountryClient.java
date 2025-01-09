package com.andwis.travel_with_anna.api.country;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryClient {

    private static final String COUNTRIES_API_PATH = "/api/v0.1/countries/currency";
    private static final String COUNTRY_CITIES_API_PATH = "/api/v0.1/countries/cities/q?country=";

    private final RestClient restClient;

    @Value("${my_apis.country.country_url}")
    private String baseUrl;

    public CountryResponse getAllCountries() {
        try {
            return restClient.get()
                    .uri(baseUrl + COUNTRIES_API_PATH)
                    .header(HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(CountryResponse.class);

        } catch (Exception e) {
            log.error("Error during fetching country names API call: {}", e.getMessage());
            return new CountryResponse();
        }
    }

    public CountryCitiesResponse getAllCountryCities(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            return new CountryCitiesResponse();
        }
        try {
            return restClient.get()
                    .uri(baseUrl + COUNTRY_CITIES_API_PATH
                            + countryName.toLowerCase())
                    .header(HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(CountryCitiesResponse.class);
        } catch (Exception e) {
            log.error("Error during fetching cities API call: {}", e.getMessage());
            return new CountryCitiesResponse();
        }
    }
}
