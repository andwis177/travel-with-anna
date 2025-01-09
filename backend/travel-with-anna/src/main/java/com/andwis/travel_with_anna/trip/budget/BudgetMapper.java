package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetMapper {

    public static @NotNull BudgetResponse toBudgetResponse(@NotNull Budget budget) {
        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getCurrency(),
                budget.getBudgetAmount(),
                budget.getTrip().getTripId()
        );
    }

    public static @NotNull List<ExpanseByCurrency> toExpansesByCurrency(@NotNull Map<String, ExpanseCalculator> expansesByCurrencyMap) {
        return convertToExpanseByCurrency(expansesByCurrencyMap);
    }

    private static @NotNull List<ExpanseByCurrency> convertToExpanseByCurrency(@NotNull Map<String, ExpanseCalculator> expansesByCurrencyMap) {
        return expansesByCurrencyMap.entrySet().stream()
                .map(entry -> ExpanseByCurrency.builder()
                        .currencyCode(entry.getKey())
                        .totalPrice(entry.getValue().getTotalPrice())
                        .totalPaid(entry.getValue().getTotalPaid())
                        .totalPriceInTripCurrency(entry.getValue().getTotalPriceInTripCurrency())
                        .totalPaidInTripCurrency(entry.getValue().getTotalPaidInTripCurrency())
                        .totalDebt(entry.getValue().getTotalDebt())
                        .build()).sorted(ExpanseByCurrency::compareTo).collect(Collectors.toList());
    }
}
