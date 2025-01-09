package com.andwis.travel_with_anna.trip.trip;

import jakarta.validation.constraints.NotNull;

public record TripRequest(
        @NotNull(message = "Trip ID cannot be null")
        Long tripId,
        String password
) {
}
