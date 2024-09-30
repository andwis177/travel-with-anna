package com.andwis.travel_with_anna.trip.backpack.item;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemMapper {

    @Contract("_ -> new")
    public static @NotNull ItemResponse toItemResponse(@NotNull Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.isPacked()
        );
    }
}
