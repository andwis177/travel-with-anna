package com.andwis.travel_with_anna.trip.note;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NoteForTripRequest {
        @Size(max = 500, message = "Expanse name should be 500 characters or less")
        private String note;
        private Long tripId;
}
