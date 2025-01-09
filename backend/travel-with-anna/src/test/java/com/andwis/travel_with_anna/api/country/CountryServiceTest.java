package com.andwis.travel_with_anna.api.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CountryServiceTest {
    @Mock
    private CountryClient countryClient;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAllCountries_shouldReturnSortedAndDeduplicatedCountries() {
        // Given
        Country country1 = new Country("USA", "USD", "USA", "US");
        Country country2 = new Country("Germany", "EUR", "RFN", "DM");
        Country country3 = new Country("USA", "USD", "USA", "US");

        CountryResponse response = new CountryResponse(Arrays.asList(country1, country2, country3));
        when(countryClient.getAllCountries()).thenReturn(response);

        // When
        List<Country> result = countryService.fetchAllCountries();

        // Then
        assertEquals(2, result.size());
        assertEquals("Germany", result.get(0).getName());
        assertEquals("USA", result.get(1).getName());

        verify(countryClient, times(1)).getAllCountries();
    }

    @Test
    void fetchAllCountries_shouldReturnEmptyListWhenResponseIsNull() {
        // Given
        when(countryClient.getAllCountries()).thenReturn(null);

        // When
        List<Country> result = countryService.fetchAllCountries();

        // Then
        assertEquals(Collections.emptyList(), result);
        verify(countryClient, times(1)).getAllCountries();
    }

    @Test
    void fetchAllCountries_shouldReturnEmptyListWhenCountriesAreNull() {
        // Given
        CountryResponse response = new CountryResponse(null);
        when(countryClient.getAllCountries()).thenReturn(response);

        // When
        List<Country> result = countryService.fetchAllCountries();

        // Then
        assertEquals(Collections.emptyList(), result);
        verify(countryClient, times(1)).getAllCountries();
    }

    @Test
    void fetchAllCountryCities_shouldReturnSortedAndDeduplicatedCities() {
        // Given
        String countryName = "TestCountry";
        City city1 = new City("Berlin");
        City city2 = new City("Amsterdam");
        City city3 = new City("Berlin");

        CountryCitiesResponse response = new CountryCitiesResponse(Arrays.asList(city1, city2, city3));
        when(countryClient.getAllCountryCities(countryName)).thenReturn(response);

        // When
        List<City> result = countryService.fetchAllCountryCities(countryName);

        // Then
        assertEquals(2, result.size());
        assertEquals("Amsterdam", result.get(0).getName());
        assertEquals("Berlin", result.get(1).getName());

        verify(countryClient, times(1)).getAllCountryCities(countryName);
    }

    @Test
    void fetchAllCountryCities_shouldReturnEmptyListWhenResponseIsNull() {
        // Given
        String countryName = "EmptyCountry";
        when(countryClient.getAllCountryCities(countryName)).thenReturn(null);

        // When
        List<City> result = countryService.fetchAllCountryCities(countryName);

        // Then
        assertEquals(Collections.emptyList(), result);
        verify(countryClient, times(1)).getAllCountryCities(countryName);
    }

    @Test
    void fetchAllCountryCities_shouldReturnEmptyListWhenCitiesAreNull() {
        // Given
        String countryName = "NoCitiesCountry";
        CountryCitiesResponse response = new CountryCitiesResponse(null);
        when(countryClient.getAllCountryCities(countryName)).thenReturn(response);

        // When
        List<City> result = countryService.fetchAllCountryCities(countryName);

        // Then
        assertEquals(Collections.emptyList(), result);
        verify(countryClient, times(1)).getAllCountryCities(countryName);
    }

    @Test
    void fetchAllCountriesCurrencies_shouldReturnSortedCurrencyList() {
        // Given
        Country country1 = new Country("USA", "USD", "USA", "US");
        Country country2 = new Country("Germany", "EUR", "RFN", "DM");
        Country country3 = new Country("Japan", "JPY", "JPY", "JPY");

        CountryResponse response = new CountryResponse(Arrays.asList(country1, country2, country3));
        when(countryClient.getAllCountries()).thenReturn(response);

        // When
        List<CountryCurrency> result = countryService.fetchAllCountriesCurrencies();

        // Then
        assertEquals(3, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        assertEquals("JPY", result.get(1).getCurrency());
        assertEquals("USD", result.get(2).getCurrency());

        verify(countryClient, times(1)).getAllCountries();
    }

    @Test
    void fetchAllCountriesCurrencies_shouldReturnEmptyListWhenCountriesAreEmpty() {
        // Given
        CountryResponse response = new CountryResponse(Collections.emptyList());
        when(countryClient.getAllCountries()).thenReturn(response);

        // When
        List<CountryCurrency> result = countryService.fetchAllCountriesCurrencies();

        // Then
        assertEquals(Collections.emptyList(), result);
        verify(countryClient, times(1)).getAllCountries();
    }

    @Test
    void deduplicateAndSort_shouldReturnSortedAndDeduplicatedList() {
        // Given
        List<String> items = Arrays.asList("Banana", "Apple", "Banana", "Cherry");

        // When
        List<String> result = countryService.deduplicateAndSort(items, String::compareTo);

        // Then
        assertEquals(3, result.size());
        assertEquals("Apple", result.get(0));
        assertEquals("Banana", result.get(1));
        assertEquals("Cherry", result.get(2));
    }

    @Test
    void deduplicateAndSort_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<String> result = countryService.deduplicateAndSort(null, String::compareTo);

        // Then
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void deduplicateAndSort_shouldReturnEmptyListWhenInputIsEmpty() {
        // When
        List<String> result = countryService.deduplicateAndSort(Collections.emptyList(), String::compareTo);

        // Then
        assertEquals(Collections.emptyList(), result);
    }
}
