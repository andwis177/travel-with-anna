package com.andwis.travel_with_anna.trip.backpack;

public class BackpackMapper {
    public static BackpackRequest toBackpackRequest(Backpack backpack, boolean isNote) {
        return new BackpackRequest(
                backpack.getBackpackId(),
                backpack.getTrip().getTripId(),
                isNote
        );
    }
}
