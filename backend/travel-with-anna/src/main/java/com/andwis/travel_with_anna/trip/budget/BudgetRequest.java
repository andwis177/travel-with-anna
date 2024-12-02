package com.andwis.travel_with_anna.trip.budget;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BudgetRequest {
    private Long budgetId;
    @Size(max = 3, message = "Currency description should be 3 characters or less")
    @NotNull(message = "Currency is required")
    @NotBlank(message = "Currency is required")
    @NotEmpty(message = "Currency is required")
    private String currency;
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.0", message = "Budget amount must be greater than or equal to 0")
    private BigDecimal toSpend;
    private Long tripId;
}
