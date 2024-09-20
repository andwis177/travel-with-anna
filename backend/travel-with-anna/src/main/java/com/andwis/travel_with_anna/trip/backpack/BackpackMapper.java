package com.andwis.travel_with_anna.trip.backpack;

public class BackpackMapper {
    public static BackpackResponse toBackpackResponse(Backpack backpack, boolean isNote) {
        return new BackpackResponse(
                backpack.getBackpackId(),
                backpack.getTrip().getTripId(),
                isNote
        );
    }
}
