package com.andwis.travel_with_anna.trip.expanse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@EqualsAndHashCode
@Getter
@Setter
@Builder
public class TripCurrencyValuesRequest {
    @NotNull(message = "Price field is required")
    @DecimalMin(value = "0.0", message = "Price amount must be greater than or equal to 0")
    private BigDecimal price;
    @NotNull(message = "Paid field is required")
    @DecimalMin(value = "0.0", message = "Paid amount must be greater than or equal to 0")
    private BigDecimal paid;
    @NotNull(message = "Exchange rate field is required")
    @DecimalMin(value = "0.0", message = "Exchange amount must be greater than or equal to 0")
    private BigDecimal exchangeRate;
}
