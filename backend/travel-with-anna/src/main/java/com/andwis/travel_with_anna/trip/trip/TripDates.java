package com.andwis.travel_with_anna.trip.trip;

import java.time.LocalDate;

public record TripDates(LocalDate startDate, LocalDate endDate, int amountOfDays) {
}
