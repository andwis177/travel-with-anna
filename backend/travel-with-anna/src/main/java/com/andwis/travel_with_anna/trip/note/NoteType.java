package com.andwis.travel_with_anna.trip.note;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum NoteType {
    DAY("day"),
    ACTIVITY("activity");

    private final String type;

    public static @Nullable NoteType fromString(String type) {
        for (NoteType noteType : NoteType.values()) {
            if (noteType.type.equalsIgnoreCase(type)) {
                return noteType;
            }
        }
        return null;
    }
}
