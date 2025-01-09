package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.Day;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TripMapper {

    public static @NotNull TripResponse toTripResponse(@NotNull Trip trip) {
        TripDates tripDates = calculateTripDates(trip);

        return new TripResponse(
                trip.getTripId(),
                trip.getBackpack().getBackpackId(),
                trip.getBudget().getBudgetId(),
                trip.getTripName(),
                tripDates.startDate(),
                tripDates.endDate(),
                tripDates.amountOfDays()
        );
    }

    private static @NotNull TripDates calculateTripDates(@NotNull Trip trip) {
        if (trip.getDays() == null) {
            return new TripDates(
                    null,
                    null,
                    0
            );
        }
        List<Day> orderedDays = trip.getDaysInOrder();
        Optional<LocalDate> startDate = orderedDays.isEmpty() ? Optional.empty() : Optional.of(orderedDays.getFirst().getDate());
        Optional<LocalDate> endDate = orderedDays.isEmpty() ? Optional.empty() : Optional.of(orderedDays.getLast().getDate());

        return new TripDates(
                startDate.orElse(null),
                endDate.orElse(null),
                orderedDays.size()
        );
    }
}
