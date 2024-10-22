package com.andwis.travel_with_anna.trip.trip;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TripCreatorRequest {
      @Size(max = 60, message = "Trip name should be 60 characters or less")
      private String tripName;
      @NotNull(message = "Currency is required")
      @NotEmpty(message = "Currency is required")
      @Size(max = 10, message = "Currency description should be 10 characters or less")
      private String currency;
      @NotNull(message = "Amount to spend is required")
      private BigDecimal toSpend;
}
