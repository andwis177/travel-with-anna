package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.CurrencyExchange;
import com.andwis.travel_with_anna.api.currency.CurrencyExchangeClient;
import com.andwis.travel_with_anna.api.currency.CurrencyExchangeResponse;
import com.andwis.travel_with_anna.api.currency.CurrencyRepository;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotFoundException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotSaveException;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private Expanse expanse;
    private ExpanseRequest expanseRequest;

    @BeforeEach
    void setUp() {
        expanse = Expanse.builder()
                .expanseId(1L)
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(50.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();
        expanseRequest = ExpanseRequest.builder()
                .expanseId(1L)
                .entityId(2L)
                .tripId(3L)
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(50.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();
    }

    @Test
    void testSaveExpanse() {
        // Given
        when(expanseRepository.save(any(Expanse.class))).thenReturn(expanse);

        // When
        Expanse savedExpanse = expanseService.save(expanse);

        // Then
        assertNotNull(savedExpanse);
        verify(expanseRepository, times(1)).save(expanse);
    }

    @Test
    void testFindByIdFound() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        Expanse foundExpanse = expanseService.findById(1L);

        // Then
        assertNotNull(foundExpanse);
        assertEquals(expanse, foundExpanse);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(ExpanseNotFoundException.class, () -> {
            expanseService.findById(1L);
        });

        // Then
        assertEquals("Expanse not found", exception.getMessage());
    }

    @Test
    void testGetCurrencyExchangeByCode() {
        // Given
        CurrencyExchange currencyExchange =  CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timeStamp(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currencyExchange));

        // When
        CurrencyExchange foundExchange = expanseService.getCurrencyExchangeByCode("USD");

        // Then
        assertNotNull(foundExchange);
        assertEquals("USD", foundExchange.getCode());
    }

    @Test
    void testGetCurrencyExchangeByCodeNotFound() {
        // Given
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CurrencyNotProvidedException.class, () -> {
            expanseService.getCurrencyExchangeByCode("EUR");
        });

        // Then
        assertEquals("EUR", exception.getMessage());
    }

    @Test
    void testCreateOrUpdateExpanse_Create() {
        // Given
        ExpanseRequest expanseRequest = ExpanseRequest.builder()
                .expanseId(0L)
                .entityId(2L)
                .tripId(3L)
                .currency("USD")
                .price(BigDecimal.valueOf(200.00))
                .paid(BigDecimal.valueOf(150.00))
                .exchangeRate(BigDecimal.valueOf(1.5))
                .build();

        when(expanseRepository.save(any(Expanse.class))).thenReturn(expanse);
        when(tripService.getTripById(3L)).thenReturn(new Trip());

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(expanseRequest,
                id -> new Object(),
                () -> expanse,
                (entity, exp) -> {});

        // Then
        assertNotNull(response);
        verify(expanseRepository).save(expanse);
    }

    @Test
    void testCreateOrUpdateExpanse_Update() {
        // Given
        when(expanseRepository.save(any(Expanse.class))).thenReturn(expanse);
        when(expanseRepository.findById(anyLong())).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(expanseRequest,
                id -> new Object(),
                Expanse::new,
                (entity, exp) -> {});

        // Then
        assertNotNull(response);
        verify(expanseRepository).save(any(Expanse.class));
    }

    @Test
    void testCreateOrUpdateExpanseNotSaved() {
        // Given
        expanseRequest.setExpanseId(null);
        expanseRequest.setEntityId(null);
        expanseRequest.setTripId(null);

        // When
        Exception exception = assertThrows(ExpanseNotSaveException.class, () -> {
            expanseService.createOrUpdateExpanse(expanseRequest,
                    id -> new Object(),
                    Expanse::new,
                    (entity, exp) -> {});
        });

        // Then
        assertEquals("Expanse not saved", exception.getMessage());
    }

    @Test
    void testGetExpanseById_Found() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.getExpanseById(1L);

        // Then
        assertNotNull(response);
        assertEquals(expanse.getExpanseId(), response.expanseId());
        verify(expanseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetExpanseById_NotFound() {
        // Given
        // When
        Exception response = assertThrows(ExpanseNotFoundException.class, () -> {
            expanseService.getExpanseById(101L);
        });

        // Then
        assertEquals("Expanse not found", response.getMessage());
        verify(expanseRepository, times(1)).findById(101L);
    }

    @Test
    void testGetExpansesForTrip() {
        // Given
        when(expanseRepository.findByTripId(3L)).thenReturn(List.of(expanse));

        // When
        List<ExpanseResponse> responseList = expanseService.getExpansesForTrip(3L);

        // Then
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals(expanse.getExpanseId(), responseList.get(0).expanseId());
        verify(expanseRepository, times(1)).findByTripId(3L);
    }

    @Test
    void testGetExpanseByEntityId_EntityWithExpanse() {
        // Given
        Long entityId = 2L;
        Function<Long, Object> getByIdFunction = id -> new Object();
        Function<Object, Expanse> getExpanseFunction = entity -> expanse;

        // When
        ExpanseResponse response = expanseService.getExpanseByEntityId(entityId, getByIdFunction, getExpanseFunction);

        // Then
        assertNotNull(response);
        assertEquals(expanse.getExpanseId(), response.expanseId());
    }

    @Test
    void testGetExpanseByEntityId_EntityWithoutExpanse() {
        // Given
        Long entityId = 2L;
        Function<Long, Object> getByIdFunction = id -> new Object();
        Function<Object, Expanse> getExpanseFunction = entity -> null;

        // When
        ExpanseResponse response = expanseService.getExpanseByEntityId(entityId, getByIdFunction, getExpanseFunction);

        // Then
        assertNull(response);
    }


    @Test
    void testGetExchangeRate() {
        // Given
        when(currencyRepository.count()).thenReturn(1L);
        when(currencyExchangeService.fetchAllExchangeRates()).thenReturn(List.of(
                CurrencyExchangeResponse.builder().code("USD")
                        .value(BigDecimal.valueOf(1.0))
                        .build(),
                CurrencyExchangeResponse.builder()
                        .code("EUR")
                        .value(BigDecimal.valueOf(0.85)).build()));

        CurrencyExchange currencyExchangeUSD = CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timeStamp(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currencyExchangeUSD));

        CurrencyExchange currencyExchangeEUR = CurrencyExchange.builder()
                .code("EUR")
                .exchangeValue(BigDecimal.valueOf(0.85))
                .timeStamp(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(currencyExchangeEUR));

        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(response);
        assertEquals(0,response.exchangeRate().compareTo(BigDecimal.valueOf(0.85))); // assuming exchange value for EUR is 0.85
    }

    @Test
    void testGetExchangeRateWithNullCurrency() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate(null, null);

        // Then
        assertEquals("Currency not provided", response.errorMsg());
        assertEquals(BigDecimal.ZERO, response.exchangeRate());
    }

    @Test
    void testGetExpanseInTripCurrency() {
        // Given
        // When
        ExpanseInTripCurrency inTripCurrency = expanseService.getExpanseInTripCurrency(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1.2)
        );

        // Then
        assertNotNull(inTripCurrency);
        assertEquals(0,inTripCurrency.price().compareTo(BigDecimal.valueOf(120)));
        assertEquals(0, inTripCurrency.paid().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    void testChangeTripCurrency() {
        // Given
        Budget budget = new Budget();
        Trip trip = new Trip();
        trip.setTripId(1L);
        budget.setTrip(trip);

        when(expanseRepository.findByTripId(1L)).thenReturn(List.of(expanse));
        when(expanseRepository.saveAll(anyList())).thenReturn(List.of(expanse));

        // When
        expanseService.changeTripCurrency(budget);

        // Then
        verify(expanseRepository).saveAll(anyList());
    }
}