package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseCalculator;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record BudgetExpensesRespond(
        BudgetRequest budgetRequest,
        List<ExpanseResponse> expanses,
        Map<String, ExpanseCalculator> sumsByCurrency,
        BigDecimal overallPriceInTripCurrency,
        BigDecimal overallPaidInTripCurrency,
        BigDecimal totalDebtInTripCurrency
) {
}
