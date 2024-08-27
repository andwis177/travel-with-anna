package com.andwis.travel_with_anna.trip.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public void saveBudget(Budget budget) {
        budgetRepository.save(budget);
    }
}
