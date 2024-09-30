package com.andwis.travel_with_anna.trip.day.activity.badge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Badge{
    ACCOMMODATION_CHECK_IN("Accommodation Check-In"),
    ACCOMMODATION_CHECK_OUT("Accommodation Check-Out"),
    TRAVELING("Traveling"),
    RENT("Rent"),
    SHOPPING("Shopping"),
    EVENT("Event"),
    EAT("Eat"),
    OTHER("Other");

    private final String badge;

    public static @Unmodifiable List<String> getBadges() {
        return List.of(
                ACCOMMODATION_CHECK_IN.badge,
                ACCOMMODATION_CHECK_OUT.badge, TRAVELING.badge,
                RENT.badge,
                SHOPPING.badge,
                EVENT.badge,
                EAT.badge,
                OTHER.badge
        );
    }
}
