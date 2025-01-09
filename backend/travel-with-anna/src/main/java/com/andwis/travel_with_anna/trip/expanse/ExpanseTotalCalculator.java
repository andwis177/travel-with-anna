package com.andwis.travel_with_anna.trip.expanse;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
public class ExpanseTotalCalculator {

    private static final BigDecimal DEFAULT_TOTAL = BigDecimal.ZERO;

    public static @NotNull ExpanseInTripCurrency calculateInTripCurrency(@NotNull List<ExpanseResponse> expenses) {
        BigDecimal totalPriceInTripCurrency = calculateTotal(expenses, ExpanseResponse::getPriceInTripCurrency);
        BigDecimal totalPaidInTripCurrency = calculateTotal(expenses, ExpanseResponse::getPaidInTripCurrency);

        return new ExpanseInTripCurrency(totalPriceInTripCurrency, totalPaidInTripCurrency);
    }

    public static BigDecimal calculateTotalDepth(@NotNull List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(expanse -> expanse.getPriceInTripCurrency().subtract(expanse.getPaidInTripCurrency()))
                .reduce(DEFAULT_TOTAL, BigDecimal::add);
    }

    private static BigDecimal calculateTotal(@NotNull List<ExpanseResponse> expenses, Function<ExpanseResponse, BigDecimal> mapper) {
        return expenses.stream()
                .map(mapper)
                .reduce(DEFAULT_TOTAL, BigDecimal::add);
    }
}
