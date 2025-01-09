package com.andwis.travel_with_anna.trip.note;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NoteType {

    DAY("day"),
    ACTIVITY("activity");

    private final String noteType;

    public static @Nullable NoteType fromString(String type) {
        return Arrays.stream(NoteType.values())
                .filter(noteType -> noteType.matchesType(type))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesType(String type) {
        return noteType.equalsIgnoreCase(type);
    }
}
