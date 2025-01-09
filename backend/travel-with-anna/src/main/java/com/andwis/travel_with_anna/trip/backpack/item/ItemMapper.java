package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.expanse.ExpanseMapper;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemMapper {

    @Contract("_ -> new")
    public static @NotNull ItemResponse toItemResponse(@NotNull Item sourceItem) {
        return new ItemResponse(
                sourceItem.getItemId(),
                sourceItem.getItemName(),
                sourceItem.getQuantity(),
                sourceItem.isPacked(),
                mapExpanseToResponse(sourceItem)
        );
    }

    private static @Nullable ExpanseResponse mapExpanseToResponse(@NotNull Item sourceItem) {
        return sourceItem.getExpanse() != null
                ? ExpanseMapper.toExpanseResponse(sourceItem.getExpanse())
                : null;
    }
}
