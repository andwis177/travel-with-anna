package com.andwis.travel_with_anna.trip.day.activity.type.travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Getter
@AllArgsConstructor
public enum TravelType {
    BIKE("Bike"),
    BOAT("Boat"),
    BUS("Bus"),
    CAR("Car"),
    PLANE("Plane"),
    TRAIN("Train"),
    WALK("Walk"),
    OTHER("Other");

    private final String type;


    public @Unmodifiable List<String> getTravelTypes() {
        return List.of(
                BIKE.type,
                BOAT.type,
                BUS.type,
                CAR.type,
                PLANE.type,
                TRAIN.type,
                WALK.type,
                OTHER.type);
    }
}
