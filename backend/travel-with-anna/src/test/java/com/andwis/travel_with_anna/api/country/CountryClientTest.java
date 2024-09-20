package com.andwis.travel_with_anna.api.country;

import com.andwis.travel_with_anna.api.client.RestClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CountryClient.class)
@Import(RestClientConfig.class)
class CountryClientTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    CountryClient countryClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${my_apis.country.country_url}")
    private String baseUrl;

    @Test
    void testFetchAllCountryNames() throws JsonProcessingException {
        // Given
        List<CountryName> data = List.of(
                CountryName.builder().name("Afghanistan").Iso2("AF").Iso3("AFG").build(),
                CountryName.builder().name("Albania").Iso2("AL").Iso3("ALB").build()
        );

        //When
        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/currency"))
                .andRespond(withSuccess
                        (objectMapper.writeValueAsString(CountryNameResponse.builder().data(data).build()),
                                MediaType.APPLICATION_JSON));

        List<CountryName> countryNames = countryClient.fetchAllCountryNames();

        //Then
        assertThat(countryNames).hasSize(2);
        assertThat(countryNames.get(0).getName()).isEqualTo("Afghanistan");
        assertThat(countryNames.get(1).getName()).isEqualTo("Albania");
    }

    @Test
    void testFetchAllCurrencies() throws JsonProcessingException {
        // Given
        List<CountryCurrency> data = List.of(
                CountryCurrency.builder().currency("USD").build(),
                CountryCurrency.builder().currency("EUR").build()
        );

        //When
        this.server
                .expect(requestTo(baseUrl + "/api/v0.1/countries/currency"))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(CountryCurrencyResponse.builder().data(data).build()),
                        MediaType.APPLICATION_JSON
                ));

        List<CountryCurrency> countryCurrencies = countryClient.fetchAllCurrencies();

        //Then
        assertThat(countryCurrencies).hasSize(2);
        assertThat(countryCurrencies.get(0).getCurrency()).isEqualTo("EUR");
        assertThat(countryCurrencies.get(1).getCurrency()).isEqualTo("USD");
    }
}