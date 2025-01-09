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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
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

        CountryResponse mockResponse = CountryResponse.builder().countries(data).build();

        this.server.expect(requestTo(baseUrl + "/api/v0.1/countries/currency"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // When
        CountryResponse response = countryClient.getAllCountries();

        // Then
        assertThat(response.getCountries()).hasSize(2);
        assertThat(response.getCountries().get(0).getName()).isEqualTo("Afghanistan");
        assertThat(response.getCountries().get(1).getName()).isEqualTo("Albania");
    }

    @Test
    void testFetchAllCountryCities_ValidCountry() throws JsonProcessingException {
        // Given
        String country = "USA";
        List<City> citiesData = List.of(
                City.builder().name("New York").build(),
                City.builder().name("Los Angeles").build()
        );

        CountryCitiesResponse mockCityResponse = CountryCitiesResponse.builder()
                .cities(citiesData)
                .build();

        this.server.expect(requestTo(baseUrl + "/api/v0.1/countries/cities/q?country=" + country.toLowerCase()))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockCityResponse), MediaType.APPLICATION_JSON));

        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities(country);

        // Then
        assertThat(response.getCities()).hasSize(2);
        assertThat(response.getCities().get(0).getName()).isEqualTo("New York");
        assertThat(response.getCities().get(1).getName()).isEqualTo("Los Angeles");
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
        String country = "Afghanistan";

        this.server.expect(requestTo(baseUrl + "/api/v0.1/countries/cities/q?country=" + country.toLowerCase()))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withServerError());

        // When
        CountryCitiesResponse response = countryClient.getAllCountryCities(country);

        // Then
        assertThat(response.getCities()).isEmpty();
    }
}