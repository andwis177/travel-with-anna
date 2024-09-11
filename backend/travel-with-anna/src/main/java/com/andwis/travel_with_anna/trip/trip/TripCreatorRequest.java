package com.andwis.travel_with_anna.trip.trip;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TripCreatorRequest {
      @Size(max = 100, message = "Trip name should be 100 characters or less")
      private String tripName;
      @Size(max = 10, message = "Currency description should be 10 characters or less")
      private String currency;
      private BigDecimal toSpend;

}
