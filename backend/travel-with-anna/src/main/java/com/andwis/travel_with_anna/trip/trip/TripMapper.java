package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.Day;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TripMapper {

    public static TripRequest toTripRequest(Trip trip) {
        List<Day> sortedDays = new ArrayList<>();
        if (trip.getDays() != null) {
            sortedDays = trip.getDays().stream()
                    .sorted(Comparator.comparing(Day::getDate))
                    .toList();
        }
        TripRequest tripRequest = TripRequest.builder()
                .tripId(trip.getTripId())
                .tripName(trip.getTripName())
                .build();
        if (!sortedDays.isEmpty()) {
            tripRequest.setStartDate(sortedDays.get(0).getDate());
            tripRequest.setEndDate(sortedDays.get(sortedDays.size() - 1).getDate());
        }
        tripRequest.setAmountOfDays(sortedDays.size());
        tripRequest.setBackpackId(trip.getBackpack().getBackpackId());
        tripRequest.setBudgetId(trip.getBudget().getBudgetId());
        return tripRequest;
    }
}
