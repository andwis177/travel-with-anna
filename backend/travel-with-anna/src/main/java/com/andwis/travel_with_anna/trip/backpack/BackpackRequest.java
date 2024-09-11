package com.andwis.travel_with_anna.trip.backpack;


public record BackpackRequest (
        Long backpackId,
        Long tripId,
        boolean isNote) {
}
