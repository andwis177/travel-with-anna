package com.andwis.travel_with_anna.trip.budget;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BudgetRequest {

    private static final String REQUIRED_CURRENCY_MESSAGE = "Currency is required";
    private static final int MAX_CURRENCY_LENGTH = 3;

    private Long budgetId;

    @NotNull(message = REQUIRED_CURRENCY_MESSAGE)
    @NotBlank(message = REQUIRED_CURRENCY_MESSAGE)
    @NotEmpty(message = REQUIRED_CURRENCY_MESSAGE)
    @Size(max = MAX_CURRENCY_LENGTH, message = "Currency description should be " + MAX_CURRENCY_LENGTH + " characters or less")
    private String currency;

    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.0", message = "Budget amount must be greater than or equal to 0")
    private BigDecimal budgetAmount;

    private Long tripId;
}
