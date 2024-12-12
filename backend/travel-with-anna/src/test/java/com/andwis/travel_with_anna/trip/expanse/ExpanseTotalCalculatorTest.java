package com.andwis.travel_with_anna.trip.expanse;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpanseTotalCalculatorTest {
    @Test
    void calculateInTripCurrency_shouldReturnZeroValuesWhenNoExpenses() {
        // Given
        List<ExpanseResponse> expenses = List.of();

        // When
        ExpanseInTripCurrency result = ExpanseTotalCalculator.calculateInTripCurrency(expenses);

        // Then
        assertEquals(BigDecimal.ZERO, result.price());
        assertEquals(BigDecimal.ZERO, result.paid());
    }

    @Test
    void calculateInTripCurrency_shouldSumPricesAndPaymentsCorrectly() {
        // Given
        List<ExpanseResponse> expenses = List.of(
                ExpanseResponse.builder()
                        .expanseId(1L)
                        .expanseName("Hotel")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("200.00"))
                        .paid(new BigDecimal("150.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("200.00"))
                        .paidInTripCurrency(new BigDecimal("150.00"))
                        .build(),

                ExpanseResponse.builder()
                        .expanseId(2L)
                        .expanseName("Flight")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("300.00"))
                        .paid(new BigDecimal("250.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("300.00"))
                        .paidInTripCurrency(new BigDecimal("250.00"))
                        .build()
        );
        // When
        ExpanseInTripCurrency result = ExpanseTotalCalculator.calculateInTripCurrency(expenses);

        // Then
        assertEquals(new BigDecimal("500.00"), result.price());
        assertEquals(new BigDecimal("400.00"), result.paid());
    }

    @Test
    void calculateTotalDepth_shouldReturnZeroWhenNoExpenses() {
        // Given
        List<ExpanseResponse> expenses = List.of();

        // When
        BigDecimal result = ExpanseTotalCalculator.calculateTotalDepth(expenses);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTotalDepth_shouldCalculateDebtCorrectly() {
        // Given
        List<ExpanseResponse> expenses = List.of(
                ExpanseResponse.builder()
                        .expanseId(1L)
                        .expanseName("Hotel")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("200.00"))
                        .paid(new BigDecimal("150.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("200.00"))
                        .paidInTripCurrency(new BigDecimal("150.00"))
                        .build(),

                ExpanseResponse.builder()
                        .expanseId(2L)
                        .expanseName("Flight")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("300.00"))
                        .paid(new BigDecimal("250.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("300.00"))
                        .paidInTripCurrency(new BigDecimal("250.00"))
                        .build()
        );

        // When
        BigDecimal result = ExpanseTotalCalculator.calculateTotalDepth(expenses);

        // Then
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void calculateTotalDepth_shouldHandleNegativeValues() {
        // Given
        List<ExpanseResponse> expenses = List.of(
                ExpanseResponse.builder()
                        .expanseId(1L)
                        .expanseName("Hotel")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("200.00"))
                        .paid(new BigDecimal("220.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("200.00"))
                        .paidInTripCurrency(new BigDecimal("220.00"))
                        .build(),

                ExpanseResponse.builder()
                        .expanseId(2L)
                        .expanseName("Flight")
                        .expanseCategory("Category")
                        .currency("USD")
                        .date("2021-01-01")
                        .price(new BigDecimal("300.00"))
                        .paid(new BigDecimal("280.00"))
                        .exchangeRate(new BigDecimal("1.0"))
                        .priceInTripCurrency(new BigDecimal("300.00"))
                        .paidInTripCurrency(new BigDecimal("280.00"))
                        .build()
        );

        // When
        BigDecimal result = ExpanseTotalCalculator.calculateTotalDepth(expenses);

        // Then
        assertEquals(new BigDecimal("-0.00"), result);
    }
}