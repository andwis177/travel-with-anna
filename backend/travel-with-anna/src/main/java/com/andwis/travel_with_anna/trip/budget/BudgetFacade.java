package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetFacade {

    private final BudgetService budgetService;

    public BudgetResponse getBudgetById(Long budgetId, UserDetails connectedUser) {
        return budgetService.getBudgetById(budgetId, connectedUser);
    }

    public BudgetExpensesRespond getBudgetExpanses(Long tripId, Long budgetId, UserDetails connectedUser) {
        return budgetService.getBudgetExpanses(tripId, budgetId, connectedUser);
    }

    public List<ExpanseTotalByBadge> getExpansesByBadgeByTripId(Long tripId, UserDetails connectedUser) {
        return budgetService.calculateExpansesByBadgeByTripId(tripId, connectedUser);
    }

    public void updateBudget(BudgetRequest request, UserDetails connectedUser) {
        budgetService.updateBudget(request, connectedUser);
    }
}
