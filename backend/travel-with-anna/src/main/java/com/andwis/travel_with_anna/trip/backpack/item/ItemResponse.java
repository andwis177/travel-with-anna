package com.andwis.travel_with_anna.trip.backpack.item;

public record ItemResponse(
        Long itemId,
        String itemName,
        String qty,
        boolean isPacked
) {
}
