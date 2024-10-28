package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityMapper;
import com.andwis.travel_with_anna.utility.NumberDistributor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import static com.andwis.travel_with_anna.trip.note.NoteMapper.toNoteResponse;

public class DayMapper {
    private static boolean isToday(@NotNull Day day) {
        return day.getDate().equals(LocalDate.now());
    }

    public static @NotNull DayResponse toDayResponse(@NotNull Day day) {
        int dayNumber = NumberDistributor.getNumber();
        return new DayResponse(
                day.getDayId(),
                day.getDate(),
                day.getDate().getDayOfWeek().toString(),
                isToday(day),
                dayNumber,
                day.getNote() != null ? toNoteResponse(day.getNote()) : null,
                day.getTrip().getTripId(),
                day.getActivity().stream().map(
                        ActivityMapper::toActivityResponse).toList()
        );
    }
}
