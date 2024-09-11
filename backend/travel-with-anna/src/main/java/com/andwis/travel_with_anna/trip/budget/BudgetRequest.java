package com.andwis.travel_with_anna.trip.budget;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BudgetRequest {
    @Size(max = 10, message = "Currency description should be 10 characters or less")
    private String currency;
    private BigDecimal toSpend;
    private Long tripId;

}
