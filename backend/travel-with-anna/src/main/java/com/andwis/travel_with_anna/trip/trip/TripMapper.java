package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.Day;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TripMapper {

    public static @NotNull TripResponse toTripResponse(@NotNull Trip trip) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        int amountOfDays;
        List<Day> sortedDays = new ArrayList<>();

        if (trip.getDays() != null) {
            sortedDays = trip.getDays().stream()
                    .sorted(Comparator.comparing(Day::getDate))
                    .toList();
        }
        if (!sortedDays.isEmpty()) {
            startDate = sortedDays.getFirst().getDate();
            endDate = sortedDays.getLast().getDate();
        }
        amountOfDays = sortedDays.size();

        return new TripResponse(
                trip.getTripId(),
                trip.getBackpack().getBackpackId(),
                trip.getBudget().getBudgetId(),
                trip.getTripName(),
                startDate,
                endDate,
                amountOfDays
        );
    }
}
