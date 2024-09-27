package com.andwis.travel_with_anna.trip.expanse;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExpanseForItemRequest {
        @Valid
        private ExpanseRequest expanseRequest;
        private Long tripId;
        private Long itemId;
}
