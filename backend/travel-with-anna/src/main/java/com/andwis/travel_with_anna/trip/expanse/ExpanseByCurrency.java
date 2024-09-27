package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;

public record ExpanseByCurrency(
        String currency,
        BigDecimal totalPrice,
        BigDecimal totalPaid,
        BigDecimal totalPriceInTripCurrency,
        BigDecimal totalPaidInTripCurrency,
        BigDecimal totalDebt
) {
}
