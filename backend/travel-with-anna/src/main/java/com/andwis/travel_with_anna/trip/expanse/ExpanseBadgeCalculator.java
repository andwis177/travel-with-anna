package com.andwis.travel_with_anna.trip.expanse;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
public class ExpanseBadgeCalculator {
    private final BigDecimal totalPriceInTripCurrency;
    private final BigDecimal totalPaidInTripCurrency;

    public ExpanseBadgeCalculator(BigDecimal totalPriceInTripCurrency, BigDecimal totalPaidInTripCurrency) {
        this.totalPriceInTripCurrency = totalPriceInTripCurrency;
        this.totalPaidInTripCurrency = totalPaidInTripCurrency;
    }

    public ExpanseBadgeCalculator add(@NotNull ExpanseBadgeCalculator other) {
        return new ExpanseBadgeCalculator(
                this.totalPriceInTripCurrency.add(other.totalPriceInTripCurrency),
                this.totalPaidInTripCurrency.add(other.totalPaidInTripCurrency)
        );
    }
}
