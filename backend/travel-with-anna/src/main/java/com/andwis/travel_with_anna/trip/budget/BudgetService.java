package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.handler.exception.BudgetNotFoundException;
import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseCalculator;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpanseService expanseService;

    public void saveBudget(Budget budget) {
        budgetRepository.save(budget);
    }

    public void updateBudget(BudgetRequest request) {
        Budget budget = findById(request.getBudgetId());
        budget.setToSpend(request.getToSpend());
        if (!request.getCurrency().equals(budget.getCurrency()) && !request.getCurrency().isEmpty()) {
            budget.setCurrency(request.getCurrency());
            expanseService.changeTripCurrency(budget);
        }
        budgetRepository.save(budget);
    }

    public Budget findById(Long budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(BudgetNotFoundException::new);
    }

    public BudgetResponse getBudgetById(Long budgetId) {
        Budget budget = findById(budgetId);
        return BudgetMapper.toBudgetResponse(budget);
    }

    public BudgetExpensesRespond getBudgetExpanses(Long tripId, Long budgetId) {

        BudgetResponse budgetRequest = getBudgetById(budgetId);
        List<ExpanseResponse> expanses = expanseService.getExpansesForTrip(tripId);
        BigDecimal overallPriceInTripCurrency = overallPriceInTripCurrency(expanses);
        BigDecimal overallPaidInTripCurrency = overallPaidInTripCurrency(expanses);
        BigDecimal totalDebt = calculateTotalDepth(expanses);
        List<ExpanseByCurrency> sumsByCurrency =
                BudgetMapper.toExpansesByCurrency(calculateSumsByCurrency(expanses));
        return new BudgetExpensesRespond(
                budgetRequest,
                expanses,
                sumsByCurrency,
                overallPriceInTripCurrency,
                overallPaidInTripCurrency,
                totalDebt,
                budgetRequest.toSpend().subtract(overallPriceInTripCurrency),
                budgetRequest.toSpend().subtract(overallPaidInTripCurrency)
        );
    }

    public Map<String, ExpanseCalculator> calculateSumsByCurrency(List<ExpanseResponse> expanses) {
        return expanses.stream()
                .collect(Collectors.groupingBy(
                        ExpanseResponse::currency,
                        Collectors.reducing(new ExpanseCalculator(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                                expanse -> new ExpanseCalculator(
                                        expanse.price() != null ? expanse.price() : BigDecimal.ZERO,
                                        expanse.paid() != null ? expanse.paid() : BigDecimal.ZERO,
                                        expanse.priceInTripCurrency() != null ? expanse.priceInTripCurrency() : BigDecimal.ZERO,
                                        expanse.paidInTripCurrency() != null ? expanse.paidInTripCurrency() : BigDecimal.ZERO,
                                        expanse.price() != null && expanse.paid() != null
                                                && expanse.price().doubleValue() > expanse.paid().doubleValue()
                                                ? expanse.price().subtract(expanse.paid()) : BigDecimal.ZERO),
                                ExpanseCalculator::add
                        )
                ));
    }

    public BigDecimal overallPriceInTripCurrency(List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(ExpanseResponse::priceInTripCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal overallPaidInTripCurrency(List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(ExpanseResponse::paidInTripCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalDepth(List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(expanse -> expanse.priceInTripCurrency().doubleValue() > expanse.paidInTripCurrency().doubleValue()
                        ? expanse.priceInTripCurrency().subtract(expanse.paidInTripCurrency())
                        : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
