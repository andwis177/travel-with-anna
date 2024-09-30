package com.andwis.travel_with_anna.trip.backpack;

public record BackpackResponse(
        Long backpackId,
        Long tripId,
        boolean isNote) {
}
