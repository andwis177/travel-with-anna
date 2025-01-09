package com.andwis.travel_with_anna.trip.day;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DayGeneratorRequest {

    private static final String START_DATE_REQUIRED = "Start date is required";
    private static final String END_DATE_REQUIRED = "End date is required";

    private Long associatedTripId;

    @NotNull(message = START_DATE_REQUIRED)
    private String startDate;

    @NotNull(message = END_DATE_REQUIRED)
    private String endDate;
}
