package com.andwis.travel_with_anna.trip.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetFacade {
    private final BudgetService budgetService;

    public void saveBudget(Budget budget) {
        budgetService.saveBudget(budget);
    }

    public BudgetRequest getBudgetById(Long budgetId) {
        return budgetService.getBudgetById(budgetId);
    }
}
