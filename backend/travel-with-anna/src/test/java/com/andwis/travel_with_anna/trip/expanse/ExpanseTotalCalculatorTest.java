package com.andwis.travel_with_anna.trip.expanse;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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
                new ExpanseResponse(1L, "Hotel", "USD", new BigDecimal("200.00"),
                        new BigDecimal("150.00"), new BigDecimal("1.0"), new BigDecimal("200.00"),
                        new BigDecimal("150.00")),
                new ExpanseResponse(2L, "Flight", "USD", new BigDecimal("300.00"),
                        new BigDecimal("250.00"), new BigDecimal("1.0"), new BigDecimal("300.00"),
                        new BigDecimal("250.00"))
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
                new ExpanseResponse(1L, "Hotel", "USD", new BigDecimal("200.00"),
                        new BigDecimal("150.00"), new BigDecimal("1.0"), new BigDecimal("200.00"),
                        new BigDecimal("150.00")),
                new ExpanseResponse(2L, "Flight", "USD", new BigDecimal("300.00"),
                        new BigDecimal("250.00"), new BigDecimal("1.0"), new BigDecimal("300.00"),
                        new BigDecimal("250.00"))
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
                new ExpanseResponse(1L, "Hotel", "USD", new BigDecimal("200.00"),
                        new BigDecimal("220.00"), new BigDecimal("1.0"), new BigDecimal("200.00"),
                        new BigDecimal("220.00")),
                new ExpanseResponse(2L, "Flight", "USD", new BigDecimal("300.00"),
                        new BigDecimal("280.00"), new BigDecimal("1.0"), new BigDecimal("300.00"),
                        new BigDecimal("280.00"))
        );

        // When
        BigDecimal result = ExpanseTotalCalculator.calculateTotalDepth(expenses);

        // Then
        assertEquals(new BigDecimal("-0.00"), result);
    }
}