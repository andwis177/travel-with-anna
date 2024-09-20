package com.andwis.travel_with_anna.trip.expanse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExpanseRequest {
       @Size(max = 60, message = "Expanse name should be 60 characters or less")
       private String expanseName;
       @NotEmpty(message = "Currency is required")
       @NotBlank(message = "Currency is required")
       @Size(max = 10, message = "Currency description should be 60 characters or less")
       private String currency;
       @NotEmpty(message = "Price field is required")
       @NotBlank(message = "Price field is required")
       private BigDecimal price;
       @NotEmpty(message = "Paid field is required")
       @NotBlank(message = "Paid field is required")
       private BigDecimal paid;
       @NotEmpty(message = "Exchange rate is required")
       @NotBlank(message = "Exchange rate is required")
       private BigDecimal exchangeRate;
       private BigDecimal priceInTripCurrency;
       private BigDecimal paidInTripCurrency;
}
