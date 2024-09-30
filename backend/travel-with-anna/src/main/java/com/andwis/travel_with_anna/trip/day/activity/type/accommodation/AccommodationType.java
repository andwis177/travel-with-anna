package com.andwis.travel_with_anna.trip.day.activity.type.accommodation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccommodationType {
    HOTEL("Hotel"),
    HOSTEL("Hostel"),
    AIRBNB("Airbnb"),
    CAMPING("Camping"),
    MOTEL("Motel"),
    FAMILY_FRIENDS("Family/Friends"),
    OTHER("Other");

    private final String name;

}
