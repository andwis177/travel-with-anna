package com.andwis.travel_with_anna.trip.backpack.item;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemRequest {

       @Size(max = Item.ITEM_NAME_MAX_LENGTH, message = "Item name should be 60 characters or less")
       private String itemName;

       @Size(max = Item.QUANTITY_MAX_LENGTH, message = "Item quantity description should be 40 characters or less")
       private String quantity;

       private boolean isPacked;
}
