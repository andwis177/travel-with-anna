package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetFacade {
    private final BudgetService budgetService;

    public void saveBudget(Budget budget) {
        budgetService.saveBudget(budget);
    }

    public BudgetResponse getBudgetById(Long budgetId) {
        return budgetService.getBudgetById(budgetId);
    }

    public BudgetExpensesRespond getBudgetExpanses(Long tripId, Long budgetId) {
        return budgetService.getBudgetExpanses(tripId, budgetId);
    }

    public List<ExpanseTotalByBadge> getExpansesByBadgeByTripId(Long tripId) {
        return budgetService.calculateExpansesByBadgeByTripId(tripId);
    }

    public void updateBudget(BudgetRequest request) {
        budgetService.updateBudget(request);
    }
}
