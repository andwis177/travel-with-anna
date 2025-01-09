package com.andwis.travel_with_anna.trip.trip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.andwis.travel_with_anna.trip.expanse.Expanse.MAX_CURRENCY_LENGTH;
import static com.andwis.travel_with_anna.trip.trip.Trip.TRIP_NAME_LENGTH;

@Getter
@Setter
@Builder
public class TripCreatorRequest {

      private final static String CURRENCY_ERROR_MSG = "Currency is required";

      @Size(max = TRIP_NAME_LENGTH, message = "Trip name should be " + TRIP_NAME_LENGTH +" characters or less")
      private String tripName;

      @NotNull(message = CURRENCY_ERROR_MSG)
      @NotEmpty(message = CURRENCY_ERROR_MSG)
      @NotBlank(message = CURRENCY_ERROR_MSG)
      @Size(max = MAX_CURRENCY_LENGTH, message = "Currency description should be 10 characters or less")
      private String currency;

      @NotNull(message = "Amount to spend is required")
      private BigDecimal toSpend;
}
