package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.trip.Trip;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DayRequest {
    private Trip trip;
    private LocalDate date;
}
