package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

    public static @NotNull List<ExpanseByCurrency> toExpansesByCurrency(@NotNull Map<String, ExpanseCalculator> expansesByCurrencyMap) {
        List<ExpanseByCurrency> expanseByCurrencies = new java.util.ArrayList<>(expansesByCurrencyMap.entrySet().stream()
                .map(entry -> ExpanseByCurrency.builder()
                        .currency(entry.getKey())
                        .totalPrice(entry.getValue().getTotalPrice())
                        .totalPaid(entry.getValue().getTotalPaid())
                        .totalPriceInTripCurrency(entry.getValue().getTotalPriceInTripCurrency())
                        .totalPaidInTripCurrency(entry.getValue().getTotalPaidInTripCurrency())
                        .totalDebt(entry.getValue().getTotalDebt())
                        .build())
                .toList());
        Collections.sort(expanseByCurrencies);
        return expanseByCurrencies;
    }
}
