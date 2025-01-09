package com.andwis.travel_with_anna.trip.backpack;

import org.jetbrains.annotations.NotNull;

public class BackpackMapper {

    public static @NotNull BackpackResponse toBackpackResponse(@NotNull Backpack backpack, boolean hasNote) {
        return new BackpackResponse(
                backpack.getBackpackId(),
                backpack.getTrip().getTripId(),
                hasNote
        );
    }
}
