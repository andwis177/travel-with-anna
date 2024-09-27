package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;

import java.math.BigDecimal;
import java.util.List;

public record BudgetExpensesRespond(
        BudgetResponse budgetResponse,
        List<ExpanseResponse> expanses,
        List<ExpanseByCurrency> sumsByCurrency,
        BigDecimal overallPriceInTripCurrency,
        BigDecimal overallPaidInTripCurrency,
        BigDecimal totalDebtInTripCurrency,
        BigDecimal priceBalanceDue,
        BigDecimal paidBalanceDue
) {
}
