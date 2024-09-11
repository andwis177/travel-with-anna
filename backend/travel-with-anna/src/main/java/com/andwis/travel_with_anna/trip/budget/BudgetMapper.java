package com.andwis.travel_with_anna.trip.budget;

public class BudgetMapper {

    public static BudgetRequest toBudgetRequest(Budget budget) {
        return new BudgetRequest(
                budget.getCurrency(),
                budget.getToSpend(),
                budget.getBudgetId()
        );
    }
}
