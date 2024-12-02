package com.andwis.travel_with_anna.api.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Country Controller tests")
class CountryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CountryClient countryClient;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void findAllCountryNames_ShouldReturnCountryNames() throws Exception {
        // Given
        Country country1 = Country.builder()
                .name("United States")
                .iso2("US")
                .iso3("USA")
                .currency("USD")
                .build();

        Country country2 = Country.builder()
                .name("Canada")
                .iso2("CA")
                .iso3("CAN")
                .currency("CAD")
                .build();

        List<Country> countryNames = List.of(country1, country2);
        when(countryClient.fetchAllCountries()).thenReturn(countryNames);
        String jsonContent = objectMapper.writeValueAsString(countryNames);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/country/names")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void findAllCountryCurrencies_ShouldReturnCountryCurrencies() throws Exception {
        // Given
        CountryCurrency currency1 = CountryCurrency.builder()
                .country("United States")
                .currency("USD")
                .build();

        CountryCurrency currency2 = CountryCurrency.builder()
                .country("Canada")
                .currency("CAD")
                .build();

        List<CountryCurrency> countryCurrencies = List.of(currency1, currency2);
        when(countryClient.fetchAllCountriesCurrencies()).thenReturn(countryCurrencies);
        String jsonContent = objectMapper.writeValueAsString(countryCurrencies);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/country/currencies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void findAllCountryCities_ShouldReturnCountryCities() throws Exception {
        // Given
        String countryName = "Afghanistan";
        City city1 = City.builder().city("Kabul").build();
        City city2 = City.builder().city("Herat").build();

        List<City> cities = List.of(city1, city2);
        when(countryClient.fetchAllCountryCities(countryName)).thenReturn(cities);
        String jsonContent = objectMapper.writeValueAsString(cities);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/country/cities")
                        .param("country", countryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }
}