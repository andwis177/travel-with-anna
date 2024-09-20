package com.andwis.travel_with_anna.trip.backpack.item;

public class ItemMapper {

    public static ItemResponse toItemResponse(Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getItem(),
                item.getQuantity(),
                item.isPacked()
        );
    }
}
