package com.andwis.travel_with_anna.trip.trip;

import java.time.LocalDate;

public record TripResponse(
        Long tripId,
        Long backpackId,
        Long budgetId,
        String tripName,
        LocalDate startDate,
        LocalDate endDate,
        int amountOfDays
) {
}
