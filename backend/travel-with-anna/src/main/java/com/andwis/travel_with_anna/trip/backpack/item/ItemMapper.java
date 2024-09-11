package com.andwis.travel_with_anna.trip.backpack.item;

public class ItemMapper {

    public static ItemRequest toItemRequest(Item item) {
        return new ItemRequest(
                item.getItemId(),
                item.getItem(),
                item.getQuantity(),
                item.isPacked()
        );
    }

}
