package com.andwis.travel_with_anna.trip.expanse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ExpanseType {

    ITEM("item"),
    ACTIVITY("activity");

    private final String type;

    private static final Map<String, ExpanseType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = Stream.of(ExpanseType.values())
                .collect(Collectors.toMap(expanseType -> expanseType.type.toLowerCase(), e -> e));
    }

    public static @Nullable ExpanseType fromString(String type) {
        return type == null ? null : LOOKUP_MAP.get(type.toLowerCase());
    }
}
