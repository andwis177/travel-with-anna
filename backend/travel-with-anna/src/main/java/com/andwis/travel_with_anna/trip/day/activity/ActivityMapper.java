package com.andwis.travel_with_anna.trip.day.activity;

import org.jetbrains.annotations.NotNull;

public class ActivityMapper {

    public static @NotNull ActivityResponse toActivityResponse(@NotNull Activity activity) {
        return new ActivityResponse(
                activity.getActivityId(),
                activity.getBadge()
        );
    }
}
