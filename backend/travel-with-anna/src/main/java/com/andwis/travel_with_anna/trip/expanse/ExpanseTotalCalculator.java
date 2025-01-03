package com.andwis.travel_with_anna.trip.expanse;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpanseTotalCalculator {
    public static @NotNull ExpanseInTripCurrency calculateInTripCurrency(@NotNull List<ExpanseResponse> expenses) {
    BigDecimal totalPriceInTripCurrency = expenses.stream()
            .map(ExpanseResponse::getPriceInTripCurrency)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalPaidInTripCurrency = expenses.stream()
            .map(ExpanseResponse::getPaidInTripCurrency)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new ExpanseInTripCurrency(totalPriceInTripCurrency, totalPaidInTripCurrency);
    }

    public static BigDecimal calculateTotalDepth(@NotNull List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(expanse -> expanse.getPriceInTripCurrency().subtract(expanse.getPaidInTripCurrency()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
