package com.andwis.travel_with_anna.trip.expanse;

import java.math.BigDecimal;

public record TripCurrencyValue(
        BigDecimal price,
        BigDecimal paid
) {
}
