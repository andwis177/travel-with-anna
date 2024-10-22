package com.andwis.travel_with_anna.trip.expanse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum ExpanseType {
    ITEM("item"),
    ACTIVITY("activity");

    private final String type;

    public static @Nullable ExpanseType fromString(String type) {
        for (ExpanseType expanseType : ExpanseType.values()) {
            if (expanseType.type.equalsIgnoreCase(type)) {
                return expanseType;
            }
        }
        return null;
    }
}
