package com.andwis.travel_with_anna.trip.budget;

import java.math.BigDecimal;

public record BudgetResponse(
        Long budgetId,
        String currency,
        BigDecimal budgetAmount,
        Long tripId
) {
}
