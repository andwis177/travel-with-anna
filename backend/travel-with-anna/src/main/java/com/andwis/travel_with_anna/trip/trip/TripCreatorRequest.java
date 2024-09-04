package com.andwis.travel_with_anna.trip.trip;

import java.math.BigDecimal;

public record TripCreatorRequest(
        String tripName,
        String currency,
        BigDecimal toSpend
) {
}
