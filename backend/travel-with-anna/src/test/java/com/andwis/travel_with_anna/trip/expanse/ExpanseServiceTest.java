package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.CurrencyExchange;
import com.andwis.travel_with_anna.api.currency.CurrencyExchangeClient;
import com.andwis.travel_with_anna.api.currency.CurrencyRepository;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Expanse Service tests")
class ExpanseServiceTest {
    @InjectMocks
    private ExpanseService expanseService;

    @Mock
    private ExpanseRepository expanseRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyExchangeClient currencyExchangeService;

    @Mock
    private TripService tripService;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveExpanse() {
        // Given
        Expanse expanse = new Expanse();
        when(expanseRepository.save(expanse)).thenReturn(expanse);

        // When
        Expanse savedExpanse = expanseService.save(expanse);

        // Then
        assertNotNull(savedExpanse);
        verify(expanseRepository, times(1)).save(expanse);
    }

    @Test
    void testFindById() {
        // Given
        Long expanseId = 1L;
        Expanse expanse = new Expanse();
        when(expanseRepository.findById(expanseId)).thenReturn(Optional.of(expanse));

        // When
        Expanse foundExpanse = expanseService.findById(expanseId);
        // Then
        assertNotNull(foundExpanse);
        verify(expanseRepository, times(1)).findById(expanseId);
    }

    @Test
    void testFindByIdThrowsExpanseNotFoundException() {
        // Given
        Long expanseId = 1L;
        when(expanseRepository.findById(expanseId)).thenReturn(Optional.empty());
        // When & Then
        assertThrows(ExpanseNotFoundException.class, () -> expanseService.findById(expanseId));
        verify(expanseRepository, times(1)).findById(expanseId);
    }

    @Test
    void testGetCurrencyExchangeByCode() {
        // Given
        String code = "USD";
        CurrencyExchange currencyExchange = new CurrencyExchange();
        when(currencyRepository.findByCode(code)).thenReturn(Optional.of(currencyExchange));

        // When
        CurrencyExchange foundCurrency = expanseService.getCurrencyExchangeByCode(code);

        // Then
        assertNotNull(foundCurrency);
        verify(currencyRepository, times(1)).findByCode(code);
    }

    @Test
    void testGetCurrencyExchangeByCodeThrowsException() {
        // Given
        String code = "USD";
        when(currencyRepository.findByCode(code)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CurrencyNotProvidedException.class, () -> expanseService.getCurrencyExchangeByCode(code));
    }

    @Test
    void testCreateOrUpdateExpanseWhenUpdatingExistingExpanse() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder().expanseId(1L).build();
        Expanse expanse = new Expanse();
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));
        when(expanseRepository.save(any(Expanse.class))).thenReturn(expanse);

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(request);

        // Then
        assertNotNull(response);
        verify(expanseRepository, times(1)).findById(1L);
        verify(expanseRepository, times(1)).save(any(Expanse.class));
    }

    @Test
    void testCreateOrUpdateExpanseWhenSavingNewExpanse() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder().itemId(1L).tripId(2L).build();
        Item item = new Item();
        Trip trip = new Trip();
        when(itemService.findById(1L)).thenReturn(item);
        when(tripService.getTripById(2L)).thenReturn(trip);
        when(expanseRepository.save(any(Expanse.class))).thenReturn(new Expanse());

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(request);

        // Then
        assertNotNull(response);
        verify(itemService, times(1)).findById(1L);
        verify(tripService, times(1)).getTripById(2L);
        verify(expanseRepository, times(1)).save(any(Expanse.class));
    }

    @Test
    void testGetExchangeRate() {
        // Given
        CurrencyExchange usd = new CurrencyExchange();
        usd.setExchangeValue(BigDecimal.ONE);

        CurrencyExchange eur = new CurrencyExchange();
        eur.setExchangeValue(BigDecimal.valueOf(0.85));

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(eur));

        // When
        BigDecimal exchangeRate = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(exchangeRate);
        assertEquals(BigDecimal.valueOf(0.85).setScale(5, RoundingMode.HALF_UP), exchangeRate);
    }

    @Test
    void testGetExchangeRateReturnsOneWhenCurrencyNotProvided() {
        // Given
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.empty());

        // When
        BigDecimal exchangeRate = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertEquals(BigDecimal.ONE, exchangeRate);
    }

    @Test
    void testChangeTripCurrency() {
        // Given
        Budget budget = new Budget();
        Trip trip = new Trip();
        trip.setTripId(1L);
        budget.setTrip(trip);
        budget.setCurrency("EUR");

        Expanse expanse1 = new Expanse();
        expanse1.setCurrency("USD");
        Expanse expanse2 = new Expanse();
        expanse2.setCurrency("GBP");

        when(expanseRepository.findByTripId(1L)).thenReturn(Arrays.asList(expanse1, expanse2));
        when(currencyRepository.findByCode(anyString())).thenReturn(Optional.of(
                new CurrencyExchange(
                        1L,
                        "USD",
                        BigDecimal.ONE,
                        LocalDateTime.now()
                )));

        // When
        expanseService.changeTripCurrency(budget);

        // Then
        verify(expanseRepository, times(1)).findByTripId(1L);
        verify(expanseRepository, times(1)).saveAll(anyList());
    }
    @Test
    void testGetExpanseByItemId() {
        // Given
        Item item = new Item();
        Expanse expanse = new Expanse();
        expanse.setExpanseId(1L);
        item.setExpanse(expanse);

        when(itemService.findById(1L)).thenReturn(item);
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.getExpanseByItemId(1L);

        // Then
        assertNotNull(response);
        verify(itemService, times(1)).findById(1L);
        verify(expanseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetExpanseById() {
        // Given
        Expanse expanse = new Expanse();
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.getExpanseById(1L);

        // Then
        assertNotNull(response);
        verify(expanseRepository, times(1)).findById(1L);
    }
}