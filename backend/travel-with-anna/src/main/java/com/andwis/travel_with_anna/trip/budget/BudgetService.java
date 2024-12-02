package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.handler.exception.BudgetNotFoundException;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.trip.expanse.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpanseService expanseService;
    private final ActivityService activityService;
    private final DayService dayService;
    private final BudgetAuthorizationService budgetAuthorizationService;

    @Transactional
    public void updateBudget(@NotNull BudgetRequest request, UserDetails connectedUser) {
        Budget budget = findById(request.getBudgetId());
        budgetAuthorizationService.verifyBudgetOwner(budget, connectedUser);
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

    public BudgetResponse getBudgetById(Long budgetId, UserDetails connectedUser) {
        Budget budget = findById(budgetId);
        budgetAuthorizationService.verifyBudgetOwner(budget, connectedUser);
        return BudgetMapper.toBudgetResponse(budget);
    }

    public BudgetExpensesRespond getBudgetExpanses(Long tripId, Long budgetId, UserDetails connectedUser) {
        BudgetResponse budgetResponse = getBudgetById(budgetId, connectedUser);
        List<ExpanseResponse> expanses = expanseService.getExpansesForTrip(tripId, connectedUser);
        BigDecimal overallPriceInTripCurrency = overallPriceInTripCurrency(expanses);
        BigDecimal overallPaidInTripCurrency = overallPaidInTripCurrency(expanses);
        BigDecimal totalDebt = ExpanseTotalCalculator.calculateTotalDepth(expanses);
        List<ExpanseByCurrency> sumsByCurrency =
                BudgetMapper.toExpansesByCurrency(calculateSumsByCurrency(expanses));
        return new BudgetExpensesRespond(
                budgetResponse,
                expanses,
                sumsByCurrency,
                overallPriceInTripCurrency,
                overallPaidInTripCurrency,
                totalDebt,
                budgetResponse.toSpend().subtract(overallPriceInTripCurrency),
                budgetResponse.toSpend().subtract(overallPaidInTripCurrency)
        );
    }

    public Map<String, ExpanseCalculator> calculateSumsByCurrency(@NotNull List<ExpanseResponse> expanses) {
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
                                                ? expanse.price().subtract(expanse.paid()) : BigDecimal.ZERO),
                                ExpanseCalculator::add
                        )
                ));
    }

    public List<ExpanseTotalByBadge> calculateExpansesByBadgeByTripId
            (@NotNull Long tripId, UserDetails connectedUser) {
        Set<Day> days = dayService.getDaysByTripId(tripId, connectedUser);
        Set<Activity> activities = new HashSet<>();
        for (Day day : days) {
            activities.addAll(activityService.getActivitiesByDayId(day.getDayId(), connectedUser));
        }
        Map<String, ExpanseBadgeCalculator> expansesByBadge = calculateExpansesByBadge(activities);
        List<ExpanseTotalByBadge> expanseTotalByBadge = convertToExpanseTotalBadge(expansesByBadge);

        return sortByType(expanseTotalByBadge);
    }

    private List<ExpanseTotalByBadge> convertToExpanseTotalBadge (@NotNull Map<String, ExpanseBadgeCalculator> expansesByBadge) {
        return expansesByBadge.entrySet().stream()
                .map(entry -> new ExpanseTotalByBadge(
                        entry.getKey(),
                        entry.getValue().getTotalPriceInTripCurrency(),
                        entry.getValue().getTotalPaidInTripCurrency())
                ).toList();
    }

    private List<ExpanseTotalByBadge> sortByType(@NotNull List<ExpanseTotalByBadge> expanseTotalByBadge) {
        return expanseTotalByBadge.stream()
                .sorted(Comparator.comparing(ExpanseTotalByBadge::getType))
                .toList();
    }

    private Map<String, ExpanseBadgeCalculator> calculateExpansesByBadge(@NotNull Set<Activity> activities) {
        return activities.stream()
                .filter(activity -> activity.getExpanse() != null)
                .collect(Collectors.groupingBy(
                        Activity::getBadge,
                        Collectors.reducing(
                                new ExpanseBadgeCalculator(BigDecimal.ZERO, BigDecimal.ZERO),
                                activity -> new ExpanseBadgeCalculator(
                                        activity.getExpanse().getPriceInTripCurrency() != null ? activity.getExpanse().getPriceInTripCurrency() : BigDecimal.ZERO,
                                        activity.getExpanse().getPaidInTripCurrency() != null ? activity.getExpanse().getPaidInTripCurrency() : BigDecimal.ZERO
                                ),
                                ExpanseBadgeCalculator::add
                        )
                ));
    }

    private BigDecimal overallPriceInTripCurrency(@NotNull List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(ExpanseResponse::priceInTripCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal overallPaidInTripCurrency(@NotNull List<ExpanseResponse> expanses) {
        return expanses.stream()
                .map(ExpanseResponse::paidInTripCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
