package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;

public record ExpanseResponse(
        Long expanseId,
        String expanseName,
        String currency,
        BigDecimal price,
        BigDecimal paid,
        BigDecimal exchangeRate,
        BigDecimal priceInTripCurrency,
        BigDecimal paidInTripCurrency
) {
}
