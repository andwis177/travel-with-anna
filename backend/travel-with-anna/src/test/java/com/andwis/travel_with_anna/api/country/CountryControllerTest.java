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

import java.util.ArrayList;
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

    @MockBean
    private CountryClient countryClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void findAllCountryNames_ShouldReturnCountryNames() throws Exception {
        // Given
        Country countryName = new Country(
                "United States",
                "US",
                "USA");
        Country countryName2 = new Country(
                "Canada",
                "CA",
                "CAN");

        List<Country> countryNames = new ArrayList<>();
        countryNames.add(countryName);
        countryNames.add(countryName2);

        when(countryClient.fetchAllCountryNames()).thenReturn(countryNames);
        String jsonContent = objectMapper.writeValueAsString(countryNames);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/country/names-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));

    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void findAllCountryCurrencies_ShouldReturnCountryCurrencies() throws Exception {
        // Given
        CountryCurrency currency = CountryCurrency.builder()
                .currency("USD")
                .build();
        CountryCurrency currency2 = CountryCurrency.builder()
                .currency("EUR")
                .build();
        List<CountryCurrency> countryCurrencies = new ArrayList<>();
        countryCurrencies.add(currency);
        countryCurrencies.add(currency2);

        when(countryClient.fetchAllCurrencies()).thenReturn(countryCurrencies);
        String jsonContent = objectMapper.writeValueAsString(countryCurrencies);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/api/country/currencies-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }
}