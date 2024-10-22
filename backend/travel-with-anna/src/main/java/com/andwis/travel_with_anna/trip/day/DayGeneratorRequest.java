package com.andwis.travel_with_anna.trip.day;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DayGeneratorRequest {
    private Long tripId;
    @NotNull(message = "Start Date is required")
    private LocalDate startDate;
    @NotNull(message = "End Date is required")
    private LocalDate endDate;
}
