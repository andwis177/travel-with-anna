package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TripMapper {

    public static TripDto toTripDto(Trip trip) {
        List<Day> sortedDays = new ArrayList<>();
        if (trip.getDays() != null) {
            sortedDays = trip.getDays().stream()
                    .sorted(Comparator.comparing(Day::getDate))
                    .toList();
        }
        TripDto tripDto = TripDto.builder()
                .tripId(trip.getId())
                .tripName(trip.getTripName())
                .build();
        if (!sortedDays.isEmpty()) {
            tripDto.setStartDate(sortedDays.get(0).getDate());
            tripDto.setEndDate(sortedDays.get(sortedDays.size() - 1).getDate());
        }
        tripDto.setAmountOfDays(sortedDays.size());
        return tripDto;
    }

    public static ViewerDto toViewerDto(User user) {
        return ViewerDto.builder()
                .viewerId(user.getUserId())
                .userName(user.getUserName())
                .build();
    }
}
