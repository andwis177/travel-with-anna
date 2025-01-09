package com.andwis.travel_with_anna.trip.expanse;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
public class ExpanseBadgeCalculator {

    private final BigDecimal totalPriceInTripCurrency;
    private final BigDecimal totalPaidInTripCurrency;

    public ExpanseBadgeCalculator add(@NotNull ExpanseBadgeCalculator other) {
        return new ExpanseBadgeCalculator(
                safeAdd(this.totalPriceInTripCurrency, other.totalPriceInTripCurrency),
                safeAdd(this.totalPaidInTripCurrency, other.totalPaidInTripCurrency)
        );
    }

    @Contract(pure = true)
    private static @NotNull BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        return defaultToZero(a).add(defaultToZero(b));
    }

    @Contract(pure = true)
    private static @NotNull BigDecimal defaultToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
