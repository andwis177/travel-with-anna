package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityResponse;
import com.andwis.travel_with_anna.trip.note.Note;

import java.time.LocalDate;
import java.util.List;

public record DayResponse(
        Long dayId,
        LocalDate date,
        String dayOfWeek,
        boolean isToday,
        int dayNumber,
        Note note,
        Long tripId,
        List<ActivityResponse> activity
) {
}
