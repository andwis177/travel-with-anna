package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import org.jetbrains.annotations.Nullable;

public record ItemResponse(
        Long itemId,
        String itemName,
        String qty,
        boolean isPacked,
        @Nullable ExpanseResponse expanse
) {
}
