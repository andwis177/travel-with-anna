package com.andwis.travel_with_anna.api.country;

import com.andwis.travel_with_anna.api.client.RestClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CountryClient.class)
@Import(RestClientConfig.class)
class CountryClientTest {
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private CountryClient countryClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${my_apis.country.country_url}")
    private String baseUrl;

    @Test
    void testFetchAllCountries() throws JsonProcessingException {
        // Given
        List<Country> data = List.of(
                Country.builder().name("Afghanistan").iso2("AF").iso3("AFG").build(),
                Country.builder().name("Albania").iso2("AL").iso3("ALB").build()
        );

        CountryResponse mockResponse = CountryResponse.builder().data(data).build();

        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/currency"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // When
        List<Country> countryNames = countryClient.fetchAllCountries();

        // Then
        assertThat(countryNames).hasSize(2);
        assertThat(countryNames.get(0).getName()).isEqualTo("Afghanistan");
        assertThat(countryNames.get(1).getName()).isEqualTo("Albania");
    }

    @Test
    void testFetchAllCountriesCurrencies() throws JsonProcessingException {
        // Given
        List<Country> countriesData = List.of(
                Country.builder().name("Afghanistan").currency("AFN").build(),
                Country.builder().name("Albania").currency("ALL").build()
        );

        CountryResponse countryResponse = CountryResponse.builder().data(countriesData).build();

        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/currency"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(objectMapper.writeValueAsString(countryResponse), MediaType.APPLICATION_JSON));

        // When
        List<CountryCurrency> countryCurrencies = countryClient.fetchAllCountriesCurrencies();

        // Then
        assertThat(countryCurrencies).hasSize(2);
        assertThat(countryCurrencies.get(0).getCurrency()).isEqualTo("AFN");
        assertThat(countryCurrencies.get(1).getCurrency()).isEqualTo("ALL");
    }

    @Test
    void testFetchAllCountryCities_ValidCountry() throws JsonProcessingException {
        // Given
        String country = "USA";
        List<City> citiesData = List.of(
                City.builder().city("New York").build(),
                City.builder().city("Los Angeles").build()
        );

        CountryCitiesResponse mockCityResponse = CountryCitiesResponse.builder()
//                .error(false)
//                .msg("Success")
                .data(citiesData)
                .build();

        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/cities/q?country=" + country.toLowerCase()))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockCityResponse), MediaType.APPLICATION_JSON));

        // When
        List<City> cities = countryClient.fetchAllCountryCities(country);

        // Then
        assertThat(cities).hasSize(2);
        assertThat(cities.get(0).getCity()).isEqualTo("Los Angeles");
        assertThat(cities.get(1).getCity()).isEqualTo("New York");
    }

    @Test
    void testFetchAllCountryCities_NullCountry() {
        // When
        List<City> cities = countryClient.fetchAllCountryCities(null);

        // Then
        assertThat(cities).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_BlankCountry() {
        // When
        List<City> cities = countryClient.fetchAllCountryCities(" ");

        // Then
        assertThat(cities).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_EmptyCountry() {
        // When
        List<City> cities = countryClient.fetchAllCountryCities("");

        // Then
        assertThat(cities).isEmpty();
    }

    @Test
    void testFetchAllCountryCities_ApiError() {
        // Given
        String country = "Afghanistan";

        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/cities/q?country=" + country.toLowerCase()))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"error\":true,\"msg\":\"Error occurred\"}", MediaType.APPLICATION_JSON));

        // When
        List<City> cities = countryClient.fetchAllCountryCities(country);

        // Then
        assertThat(cities).isEmpty();
    }
}