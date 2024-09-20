package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.CurrencyExchange;
import com.andwis.travel_with_anna.api.currency.CurrencyRepository;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Expanse Service tests")
class ExpanseServiceTest {

    @Autowired
    private ExpanseService expanseService;

    @Autowired
    private ExpanseRepository expanseRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CurrencyRepository currencyRepository;


    private Trip trip;
    private Item item;

    @BeforeEach
    void setUp() {
        trip = Trip.builder()
                .tripName("Vacation")
                .expanses(new ArrayList<>())
                .days(new ArrayList<>())
                .build();
        tripRepository.save(trip);

        item = Item.builder()
                .item("Flight Ticket")
                .quantity("1")
                .build();
        itemRepository.save(item);

        currencyRepository.deleteAll();

        CurrencyExchange usdToEur = CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(0.9))
                .timeStamp(LocalDateTime.now())
                .build();
        CurrencyExchange eurToGbp = CurrencyExchange.builder()
                .code("EUR")
                .exchangeValue(BigDecimal.valueOf(0.8))
                .timeStamp(LocalDateTime.now())
                .build();

        currencyRepository.saveAll(List.of(usdToEur, eurToGbp));
    }

    @AfterEach
    void tearDown() {
        expanseRepository.deleteAll();
        itemRepository.deleteAll();
        tripRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateNewExpanse() {
        // Given
        ExpanseForItemRequest expanseItemCreator = new ExpanseForItemRequest(
                ExpanseRequest.builder()
                        .expanseName("Flight Ticket")
                        .currency("USD")
                        .price(BigDecimal.valueOf(300))
                        .paid(BigDecimal.valueOf(300))
                        .exchangeRate(BigDecimal.valueOf(1.0))
                        .build(),
                trip.getTripId(),
                item.getItemId()
        );

        // When
        ExpanseResponse createdExpanse = expanseService.createOrUpdateExpanse(expanseItemCreator);
        entityManager.flush();
        Expanse savedExpanse = expanseRepository.findById(createdExpanse.expanseId()).orElseThrow();

        // Then
        assertNotNull(savedExpanse);
        assertEquals("Flight Ticket", savedExpanse.getExpanseName());
        assertEquals(BigDecimal.valueOf(300), savedExpanse.getPrice());
        assertEquals(item.getItemId(), savedExpanse.getItem().getItemId());
        assertEquals(BigDecimal.valueOf(300.0), createdExpanse.paidInTripCurrency());
        assertEquals(BigDecimal.valueOf(300.0), createdExpanse.priceInTripCurrency());
    }

    @Test
    @Transactional
    void testUpdateExpanse() {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Hotel")
                .currency("USD")
                .price(BigDecimal.valueOf(500))
                .paid(BigDecimal.valueOf(500))
                .exchangeRate(BigDecimal.valueOf(1.0))
                .item(item)
                .trip(trip)
                .build();
        expanseRepository.save(expanse);
        item.setExpanse(expanse);

        ExpanseForItemRequest expanseItemCreator = new ExpanseForItemRequest(
                ExpanseRequest.builder()
                        .expanseName("Updated Hotel")
                        .currency("USD")
                        .price(BigDecimal.valueOf(600))
                        .paid(BigDecimal.valueOf(600))
                        .exchangeRate(BigDecimal.valueOf(1.0))
                        .build(),
                trip.getTripId(),
                item.getItemId()
        );

        // When
        ExpanseResponse updatedExpanse = expanseService.createOrUpdateExpanse(expanseItemCreator);
        Expanse savedExpanse = expanseRepository.findById(updatedExpanse.expanseId()).orElseThrow();

        // Then
        assertNotNull(savedExpanse);
        assertEquals("Updated Hotel", savedExpanse.getExpanseName());
        assertEquals(BigDecimal.valueOf(600), savedExpanse.getPrice());
        assertEquals(BigDecimal.valueOf(600.0), updatedExpanse.paidInTripCurrency());
        assertEquals(BigDecimal.valueOf(600.0), updatedExpanse.priceInTripCurrency());
    }

    @Test
    @Transactional
    void testAddExpanseToTrip() {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Dinner")
                .currency("USD")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(100))
                .exchangeRate(BigDecimal.valueOf(1.0))
                .build();

        // When
        trip.addExpanse(expanse);
        expanseRepository.save(expanse);
        entityManager.flush();
        Expanse savedExpanse = expanseRepository.findById(expanse.getExpanseId()).orElseThrow();

        // Then
        assertTrue(trip.getExpanses().contains(savedExpanse));
        assertEquals(trip.getTripId(), savedExpanse.getTrip().getTripId());
    }

    @Test
    @Transactional
    void testGetExpanseForItemWithValidId() {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Dinner")
                .currency("USD")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(100))
                .exchangeRate(BigDecimal.valueOf(1.0))
                .item(item)
                .trip(trip)
                .build();
        expanseRepository.save(expanse);
        item.setExpanse(expanse);

        // When
        ExpanseResponse foundExpanse = expanseService.getExpanseForItem(item.getItemId());
        entityManager.flush();

        // Then
        assertNotNull(foundExpanse);
        assertEquals("Dinner", foundExpanse.expanseName());
        assertEquals(BigDecimal.valueOf(100), foundExpanse.price());
    }

    @Test
    @DisplayName("Test get expanse for item returns null if no expanse")
    void testGetExpanseForItemReturnsNullIfNoExpanse() {
        // Given
        Long itemIdWithoutExpanse = item.getItemId();

        // When
        ExpanseResponse foundExpanse = expanseService.getExpanseForItem(itemIdWithoutExpanse);

        // Then
        assertNull(foundExpanse);
    }

    @Test
    @DisplayName("Test get expanse for item returns null for invalid itemId")
    void testGetExpanseForItemReturnsNullForInvalidId() {
        // Given
        Long invalidItemId = 999L;

        // When
        ExpanseResponse foundExpanse = expanseService.getExpanseForItem(invalidItemId);

        // Then
        assertNull(foundExpanse);
    }

    @Test
    @DisplayName("Test get expanse for item returns null for null itemId")
    void testGetExpanseForItemReturnsNullForNullId() {
        // Given
        // When
        ExpanseResponse foundExpanse = expanseService.getExpanseForItem(null);

        // Then
        assertNull(foundExpanse);
    }

    @Test
    @DisplayName("Test get expanse for item returns null for zero itemId")
    void testGetExpanseForItemReturnsNullForZeroId() {
        // Given
        // When
        ExpanseResponse foundExpanse = expanseService.getExpanseForItem(0L);

        // Then
        assertNull(foundExpanse);
    }

    @Test
    @DisplayName("Test getExchangeRate with valid currencies")
    @Transactional
    void testGetExchangeRate() {
        // Given
        // When
        BigDecimal exchangeRate = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(exchangeRate);
        assertEquals(BigDecimal.valueOf(0.88889), exchangeRate);
    }

    @Test
    @DisplayName("Test getExchangeRate with invalid currencies")
    @Transactional
    void testGetExchangeRateInvalidCurrency() {
        // Given
        // When
        BigDecimal exchangeRate = expanseService.getExchangeRate("USD", "GBP");

        // Then
        assertEquals(BigDecimal.ZERO, exchangeRate);
    }

    @Test
    @Transactional
    void testGetTripCurrencyValues() {
        // Given
        BigDecimal price = BigDecimal.valueOf(100);
        BigDecimal paid = BigDecimal.valueOf(90);
        BigDecimal exchangeRate = BigDecimal.valueOf(0.9);

        // When
        ExpanseInTripCurrency tripCurrencyValues = expanseService.getExpanseInTripCurrency(price, paid, exchangeRate);

        // Then
        assertNotNull(tripCurrencyValues);
        assertEquals(BigDecimal.valueOf(90.00)
                .setScale(2, RoundingMode.HALF_UP), tripCurrencyValues.price());
        assertEquals(BigDecimal.valueOf(81.00)
                .setScale(2, RoundingMode.HALF_UP), tripCurrencyValues.paid());
    }
}