package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.DayGeneratorRequest;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TripEditRequest {
    private DayGeneratorRequest dayGeneratorRequest;
    @Size(max = 60, message = "Trip name should be 60 characters or less")
    private String tripName;
}
