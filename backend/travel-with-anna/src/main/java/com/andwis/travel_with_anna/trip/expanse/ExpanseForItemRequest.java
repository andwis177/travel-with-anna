package com.andwis.travel_with_anna.trip.expanse;

public record ExpanseForItemRequest(
        ExpanseRequest expanseItem,
        Long tripId,
        Long itemId) {
}
