package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityMapper;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class DayMapper {

    private static boolean isToday(@NotNull Day day) {
        return day.getDate().equals(LocalDate.now());
    }

    public static @NotNull DayResponse toDayResponse(@NotNull Day day) {
        return new DayResponse(
                day.getDayId(),
                day.getDate(),
                day.getDate().getDayOfWeek().toString(),
                isToday(day),
                day.getNote(),
                day.getTrip().getTripId(),
                day.getActivity().stream().map(
                        ActivityMapper::toActivityResponse).toList()
        );
    }
}
