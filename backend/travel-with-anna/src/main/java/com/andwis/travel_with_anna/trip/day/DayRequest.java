package com.andwis.travel_with_anna.trip.day;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DayRequest {
    private Long entityId;
    private LocalDate date;
}
