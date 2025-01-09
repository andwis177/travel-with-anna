package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityListResponse;
import com.andwis.travel_with_anna.trip.day.activity.ActivityMapper;
import com.andwis.travel_with_anna.utility.NumberDistributor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.andwis.travel_with_anna.trip.note.NoteMapper.toNoteResponse;

public class DayMapper {

    private static String getDayOfWeek(@NotNull LocalDate date) {
        return date.getDayOfWeek().toString();
    }

    private static boolean isToday(@NotNull LocalDate date) {
        return date.equals(LocalDate.now());
    }

    public static @NotNull DayResponse toDayResponse(@NotNull Day day) {
        LocalDate date = day.getDate();
        Long tripId = day.getTrip().getTripId();
        int dayNumber = NumberDistributor.getNumber();
        return new DayResponse(
                day.getDayId(),
                date,
                getDayOfWeek(date),
                isToday(date),
                dayNumber,
                day.getNote() != null ? toNoteResponse(day.getNote()) : null,
                tripId,
                toActivityResponse(day)
        );
    }

    private static ActivityListResponse toActivityResponse(@NotNull Day day) {
        List<Activity> activities = new ArrayList<>(day.getActivities());
        Collections.sort(activities);
        return new ActivityListResponse(activities.stream().map(
                ActivityMapper::toActivityResponse).toList());
    }
}
