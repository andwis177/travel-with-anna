package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.day.DayGeneratorRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TripEditRequest {
    private DayGeneratorRequest dayGeneratorRequest;
    private String tripName;
}
