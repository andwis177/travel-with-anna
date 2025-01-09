package com.andwis.travel_with_anna.trip.expanse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("ExpanseMapper Tests")
class ExpanseMapperTest {

    private static final String EXPANSE_NAME = "Test Expanse";
    private static final String EXPANSE_CATEGORY = "Test Category";
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = new BigDecimal("100.00");
    private static final BigDecimal PAID = new BigDecimal("50.00");
    private static final BigDecimal EXCHANGE_RATE = new BigDecimal("1.2");
    private static final String DATE = "2025-01-01";

    private ExpanseRequest expanseRequest;
    private Expanse expanse;

    @BeforeEach
    void setUp() {
        expanseRequest = ExpanseRequest.builder()
                .expanseName(EXPANSE_NAME)
                .expanseCategory(EXPANSE_CATEGORY)
                .currency(CURRENCY)
                .price(PRICE)
                .paid(PAID)
                .exchangeRate(EXCHANGE_RATE)
                .date(DATE)
                .build();

        expanse = Expanse.builder()
                .expanseId(1L)
                .expanseName(EXPANSE_NAME)
                .expanseCategory(EXPANSE_CATEGORY)
                .currency(CURRENCY)
                .price(PRICE)
                .paid(PAID)
                .exchangeRate(EXCHANGE_RATE)
                .build();
    }

    @Test
    void testMapToExpanse_withNullExpanseRequest_throwsIllegalArgumentException() {
        // Given
        ExpanseRequest nullRequest = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ExpanseMapper.mapToExpanse(expanse, nullRequest));

        assertEquals("Expanse and ExpanseRequest cannot be null", exception.getMessage());
    }

    @Test
    void testMapToExpanse_createsExpanseFromRequest() {
        // Given
        // When
        ExpanseMapper.mapToExpanse(null, expanseRequest);

        // Then
        assertNotNull(expanse);
        assertEquals(EXPANSE_NAME, expanse.getExpanseName());
        assertEquals(EXPANSE_CATEGORY, expanse.getExpanseCategory());
        assertEquals(CURRENCY, expanse.getCurrency());
        assertEquals(PRICE, expanse.getPrice());
        assertEquals(PAID, expanse.getPaid());
        assertEquals(EXCHANGE_RATE, expanse.getExchangeRate());
    }

    @Test
    void testUpdateExpanse_updatesExpanseWithRequestData() {
        // Given
        expanseRequest = ExpanseRequest.builder()
                .expanseName("Updated Expanse")
                .expanseCategory("Updated Category")
                .currency("EUR")
                .price(new BigDecimal("200.00"))
                .paid(new BigDecimal("100.00"))
                .exchangeRate(new BigDecimal("1.5"))
                .date("2025-02-01")
                .build();

        // When
        ExpanseMapper.updateExpanse(expanse, expanseRequest);

        // Then
        assertEquals("Updated Expanse", expanse.getExpanseName());
        assertEquals("Updated Category", expanse.getExpanseCategory());
        assertEquals("EUR", expanse.getCurrency());
        assertEquals(new BigDecimal("200.00"), expanse.getPrice());
        assertEquals(new BigDecimal("100.00"), expanse.getPaid());
        assertEquals(new BigDecimal("1.5"), expanse.getExchangeRate());
        assertEquals("2025-02-01", expanse.getDate().toString());
    }

    @Test
    void testToExpanseResponse_createsExpanseResponse() {
        // Given
        expanse.setDate(LocalDate.parse(DATE));

        // When
        ExpanseResponse response = ExpanseMapper.toExpanseResponse(expanse);

        // Then
        assertNotNull(response);
        assertEquals(expanse.getExpanseId(), response.getExpanseId());
        assertEquals(EXPANSE_NAME, response.getExpanseName());
        assertEquals(EXPANSE_CATEGORY, response.getExpanseCategory());
        assertEquals(DATE, response.getDate());
        assertEquals(CURRENCY, response.getCurrency());
        assertEquals(PRICE, response.getPrice());
        assertEquals(PAID, response.getPaid());
        assertEquals(EXCHANGE_RATE, response.getExchangeRate());
        assertNotNull(response.getPriceInTripCurrency());
        assertNotNull(response.getPaidInTripCurrency());
    }

    @Test
    void testMapToExpanseResponseList_createsListOfExpanseResponse() {
        // Given
        List<Expanse> expanses = Arrays.asList(expanse, expanse);

        // When
        List<ExpanseResponse> responseList = ExpanseMapper.mapToExpanseResponseList(expanses);

        // Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals(EXPANSE_NAME, responseList.getFirst().getExpanseName());
        assertEquals(EXPANSE_CATEGORY, responseList.getFirst().getExpanseCategory());
    }

    @Test
    void testMapToExpanse_createsExpanseFromRequest_withNullFields() {
        // Given
        expanseRequest = ExpanseRequest.builder()
                .expanseName("Test Expanse")
                .expanseCategory("Test Category")
                .currency("USD")
                .price(null)
                .paid(null)
                .exchangeRate(null)
                .date(null)
                .build();

        // When
        ExpanseMapper.mapToExpanse(expanse, expanseRequest);

        // Then
        assertNotNull(expanse);
        assertEquals(BigDecimal.ZERO, expanse.getPrice());
        assertEquals(BigDecimal.ZERO, expanse.getPaid());
        assertEquals(BigDecimal.ONE, expanse.getExchangeRate());
        assertNull(expanse.getDate());
    }

    @Test
    void testMapToExpanse_updatesExpanseWithNullDate() {
        // Given
        expanseRequest.setDate(null);

        // When
        ExpanseMapper.updateExpanse(expanse, expanseRequest);

        // Then
        assertNull(expanse.getDate());
    }
}