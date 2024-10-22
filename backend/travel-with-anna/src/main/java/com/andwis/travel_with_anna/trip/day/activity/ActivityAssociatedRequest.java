package com.andwis.travel_with_anna.trip.day.activity;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityAssociatedRequest {
    @Valid
    private ActivityRequest firstRequest;
    @Valid
    private ActivityRequest secondRequest;
}
