package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.DayGeneratorRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.andwis.travel_with_anna.trip.trip.Trip.TRIP_NAME_LENGTH;

@Getter
@Setter
@Builder
public class TripEditRequest {

    private @Valid DayGeneratorRequest dayGeneratorRequest;

    @Size(max = TRIP_NAME_LENGTH, message = "Trip name should be " + TRIP_NAME_LENGTH + " characters or less")
    private String tripName;
}
