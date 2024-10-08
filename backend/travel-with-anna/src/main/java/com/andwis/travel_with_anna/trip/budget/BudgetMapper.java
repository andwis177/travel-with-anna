package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class BudgetMapper {

    public static @NotNull BudgetResponse toBudgetResponse(@NotNull Budget budget) {
        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getCurrency(),
                budget.getToSpend(),
                budget.getTrip().getTripId()
        );
    }

    public static List<ExpanseByCurrency> toExpansesByCurrency(@NotNull Map<String, ExpanseCalculator> expansesByCurrencyMap) {
        return expansesByCurrencyMap.entrySet().stream()
                .map(entry -> new ExpanseByCurrency(
                        entry.getKey(),
                        entry.getValue().getTotalPrice(),
                        entry.getValue().getTotalPaid(),
                        entry.getValue().getTotalPriceInTripCurrency(),
                        entry.getValue().getTotalPaidInTripCurrency(),
                        entry.getValue().getTotalDebt()))
                .toList();
    }
}
