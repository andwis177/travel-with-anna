package com.andwis.travel_with_anna.trip.day;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DayAddRequest {
    private Long tripId;
    private boolean isFirst;
}
