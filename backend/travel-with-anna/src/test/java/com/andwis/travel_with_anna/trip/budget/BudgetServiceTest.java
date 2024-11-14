package com.andwis.travel_with_anna.trip.budget;


import com.andwis.travel_with_anna.handler.exception.BudgetNotFoundException;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.trip.expanse.*;
import com.andwis.travel_with_anna.trip.trip.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Budget Service tests")
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private ExpanseService expanseService;
    @Mock
    private ActivityService activityService;
    @Mock
    private DayService dayService;
    @InjectMocks
    private BudgetService budgetService;
    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budget = Budget.builder()
                .budgetId(1L)
                .toSpend(new BigDecimal("1000.00"))
                .currency("USD")
                .trip(new Trip())
                .build();
        Trip trip = new Trip();
        trip.setTripId(1L);
        budget.setTrip(trip);
    }

    @Test
    void testSaveBudget() {
        // Given
        // When
        budgetService.saveBudget(budget);

        // Then
        verify(budgetRepository).save(budget);
    }

    @Test
    void testUpdateBudget_UpdatesCurrencyAndCallsChangeTripCurrency() {
        // Given
        BudgetRequest request = BudgetRequest.builder()
                .budgetId(1L)
                .toSpend(new BigDecimal("1200.00"))
                .currency("EUR")
                .build();
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        // When
        budgetService.updateBudget(request);

        // Then
        assertEquals(new BigDecimal("1200.00"), budget.getToSpend());
        assertEquals("EUR", budget.getCurrency());
        verify(expanseService).changeTripCurrency(budget);
        verify(budgetRepository).save(budget);
    }

    @Test
    void testUpdateBudget_BudgetNotFound() {
        // Given
        BudgetRequest request = BudgetRequest.builder()
                .budgetId(1L)
                .build();
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BudgetNotFoundException.class, () -> budgetService.updateBudget(request));
    }

    @Test
    void testFindById() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        // When
        Budget foundBudget = budgetService.findById(1L);

        // Then
        assertEquals(budget, foundBudget);
    }

    @Test
    void testFindById_BudgetNotFound() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BudgetNotFoundException.class, () -> budgetService.findById(1L));
    }

    @Test
    void testUpdateBudget() {
        // Given
        BudgetRequest request = new BudgetRequest(
                1L,"USD",  new BigDecimal("1500.00"), 1L);
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
        doNothing().when(expanseService).changeTripCurrency(any());

        // When
        budgetService.updateBudget(request);

        // Then
        assertEquals(new BigDecimal("1500.00"), budget.getToSpend());
        assertEquals("USD", budget.getCurrency());
        verify(budgetRepository).save(budget);
    }

    @Test
    void testGetBudgetExpanses() {
        // Given
        Long tripId = 1L;
        Long budgetId = 1L;
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        List<ExpanseResponse> expanses = new ArrayList<>();
        ExpanseResponse expanse1 =  new ExpanseResponse(1L,"First Expanse", "USD",
                new BigDecimal("100.00"), new BigDecimal("50.00"), new BigDecimal("2.00"),
                new BigDecimal("200.00"), new BigDecimal("100.00"));
        expanses.add(expanse1);
        when(expanseService.getExpansesForTrip(tripId)).thenReturn(expanses);

        // When
        BudgetExpensesRespond budgetExpenses = budgetService.getBudgetExpanses(tripId, budgetId);

        // Then
        assertNotNull(budgetExpenses);
        assertEquals(budget.getBudgetId(), budgetExpenses.budgetResponse().budgetId());
        assertEquals(expanses, budgetExpenses.expanses());
        assertEquals(new BigDecimal("200.00"), budgetExpenses.overallPriceInTripCurrency());
    }

    @Test
    void testCalculateSumsByCurrency() {
        // Given
        List<ExpanseResponse> expanses = new ArrayList<>();
        ExpanseResponse expanse1 =  new ExpanseResponse(1L,"First Expanse", "USD",
                new BigDecimal("100.00"), new BigDecimal("50.00"), new BigDecimal("2.00"),
                new BigDecimal("200.00"), new BigDecimal("100.00"));
        expanses.add(expanse1);

        // When
        Map<String, ExpanseCalculator> sums = budgetService.calculateSumsByCurrency(expanses);

        // Then
        assertNotNull(sums);
        assertTrue(sums.containsKey("USD"));
        assertEquals(new BigDecimal("100.00"), sums.get("USD").getTotalPrice());
        assertEquals(new BigDecimal("50.00"), sums.get("USD").getTotalPaid());
    }

    @Test
    void testCalculateExpansesByBadgeByTripId() {
        // Given
        Long tripId = 1L;
        Set<Day> days = new HashSet<>();
        Day day = new Day();
        day.setDayId(1L);
        days.add(day);

        Activity activity1 = new Activity();
        activity1.setBadge("badge1");
        activity1.setExpanse(Expanse.builder()
                .price(new BigDecimal("100.00"))
                .paid(new BigDecimal("50.00"))
                .build());
        Activity activity2 = new Activity();
        activity2.setBadge("badge2");
        activity2.setExpanse(Expanse.builder()
                .price(new BigDecimal("100.00"))
                .paid(new BigDecimal("70.00"))
                .build());
        day.setActivities(Set.of(activity1, activity2));
        when(dayService.getDaysByTripId(tripId)).thenReturn(days);
        when(activityService.getActivitiesByDayId(any())).thenReturn(List.of(activity1, activity2));

        // When
        List<ExpanseTotalByBadge> result = budgetService.calculateExpansesByBadgeByTripId(tripId);

        // Then
        assertEquals(2, result.size());
        assertEquals("badge1", result.get(0).getType());
        assertEquals("badge2", result.get(1).getType());
    }
}