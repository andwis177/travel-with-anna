package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;

public record ItemResponse(
        Long itemId,
        String itemName,
        String qty,
        boolean isPacked,
        ExpanseResponse expanse
) {
}
