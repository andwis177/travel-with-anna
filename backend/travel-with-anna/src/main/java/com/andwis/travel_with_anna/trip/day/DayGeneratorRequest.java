package com.andwis.travel_with_anna.trip.day;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DayGeneratorRequest {
    private Long tripId;
    @NotNull(message = "Start date is required")
    private String startDate;
    @NotNull(message = "End date is required")
    private String endDate;
}
