package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Country Client tests")
class CountryClientTest {
    @Autowired
    private CountryClient countryClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Test
    void testFetchAllCountries(){
        // Given
        // When
        CountryResponse response = countryClient.getAllCountries();

        // Then
        Assertions.assertFalse(response.getCountries().isEmpty());
        List<String> countriesNames = response.getCountries().stream().map(Country::getName).toList();
        Assertions.assertTrue(countriesNames.containsAll(List.of("Poland", "United States")));
    }

    @Test
    void testFetchAllCountryCities_ValidCountry() throws JsonProcessingException {
        // Given
        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities("Poland");

        // Then
        Assertions.assertFalse(response.getCities().isEmpty());
        List<String> citiesNames = response.getCities().stream().map(City::getName).toList();
        Assertions.assertTrue(citiesNames.containsAll(List.of("Sopot", "Warszawa")));
    }

    @Test
    void testFetchAllCountryCities_NullCountry() {
        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities(null);

        // Then
        assertThat(response.getCities()).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_BlankCountry() {
        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities(" ");

        // Then
        assertThat(response.getCities()).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_EmptyCountry() {
        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities("");

        // Then
        assertThat(response.getCities()).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_ApiError() {
        // Given
        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities("Not existing Country");

        // Then
        assertThat(response.getCities()).isEmpty();
    }
}