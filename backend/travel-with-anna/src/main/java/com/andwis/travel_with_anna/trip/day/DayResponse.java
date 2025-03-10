package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.ActivityListResponse;
import com.andwis.travel_with_anna.trip.note.NoteResponse;

import java.time.LocalDate;

public record DayResponse(
        Long dayId,
        LocalDate date,
        String dayOfWeek,
        boolean isToday,
        int dayNumber,
        NoteResponse note,
        Long tripId,
        ActivityListResponse activities
) {
}
