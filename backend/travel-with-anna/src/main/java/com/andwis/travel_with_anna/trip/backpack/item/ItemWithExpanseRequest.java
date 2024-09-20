package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.expanse.ExpanseRequest;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
public class ItemWithExpanseRequest {
    @Size(max = 60, message = "Item name should be 60 characters or less")
    private String item;
    @Size(max = 40, message = "Item qty description should be 40 characters or less")
    private String qty;
    private boolean isPacked;
    private ExpanseRequest expanseRequest;
}
