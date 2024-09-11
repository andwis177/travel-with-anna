package com.andwis.travel_with_anna.trip.trip;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TripRequest {
        private Long tripId;
        private Long backpackId;
        private Long budgetId;
        @Size(max = 100, message = "Trip name should be 100 characters or less")
        private String tripName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int amountOfDays;
}
