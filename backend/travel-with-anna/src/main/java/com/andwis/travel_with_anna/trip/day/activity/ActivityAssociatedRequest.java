package com.andwis.travel_with_anna.trip.day.activity;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActivityAssociatedRequest {
    @Valid
    private ActivityRequest firstRequest;
    @Valid
    private ActivityRequest secondRequest;
    boolean isAddressSeparated;
}
