package com.andwis.travel_with_anna.trip.day;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
