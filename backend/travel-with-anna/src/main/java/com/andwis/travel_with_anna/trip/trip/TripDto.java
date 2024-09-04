package com.andwis.travel_with_anna.trip.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripDto {
        private Long tripId;
        private String tripName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int amountOfDays;
}
