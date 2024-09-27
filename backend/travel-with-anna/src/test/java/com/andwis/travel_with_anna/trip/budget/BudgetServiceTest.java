package com.andwis.travel_with_anna.trip.budget;


import com.andwis.travel_with_anna.api.currency.CurrencyExchange;
import com.andwis.travel_with_anna.api.currency.CurrencyRepository;
import com.andwis.travel_with_anna.handler.exception.BudgetNotFoundException;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Budget Service tests")
class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpanseRepository expanseRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Trip trip;
    private Budget budget;

    @BeforeEach
    void setUp() {
        trip = Trip.builder()
                .tripName("Vacation")
                .expanses(new ArrayList<>())
                .days(new ArrayList<>())
                .build();
        tripRepository.save(trip);

        budget = Budget.builder()
                .toSpend(BigDecimal.valueOf(1000))
                .currency("USD")
                .trip(trip)
                .build();
        budgetRepository.save(budget);

        currencyRepository.deleteAll();

        CurrencyExchange usdToEur = CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(0.9))
                .timeStamp(LocalDateTime.now())
                .build();
        currencyRepository.save(usdToEur);
    }

    @AfterEach
    void tearDown() {
        expanseRepository.deleteAll();
        budgetRepository.deleteAll();
        tripRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    @Test
    @Transactional
    void testSaveBudget() {
        // Given
        Budget newBudget = Budget.builder()
                .toSpend(BigDecimal.valueOf(500))
                .currency("EUR")
                .trip(trip)
                .build();

        // When
        budgetService.saveBudget(newBudget);
        entityManager.flush();

        // Then
        assertNotNull(newBudget.getBudgetId());
        assertEquals("EUR", newBudget.getCurrency());
        assertEquals(BigDecimal.valueOf(500), newBudget.getToSpend());
    }

    @Test
    @Transactional
    void testUpdateBudget() {
        // Given
        BudgetRequest request = new BudgetRequest(
                budget.getBudgetId(),
                "EUR",
                BigDecimal.valueOf(1200),
                trip.getTripId()
        );
        request.setBudgetId(budget.getBudgetId());
        request.setToSpend(BigDecimal.valueOf(1200));
        request.setCurrency("EUR");

        // When
        budgetService.updateBudget(request);
        entityManager.flush();
        Budget updatedBudget = budgetRepository.findById(budget.getBudgetId()).orElseThrow();

        // Then
        assertEquals(BigDecimal.valueOf(1200), updatedBudget.getToSpend());
        assertEquals("EUR", updatedBudget.getCurrency());
    }

    @Test
    void testFindById() {
        // When
        Budget foundBudget = budgetService.findById(budget.getBudgetId());

        // Then
        assertNotNull(foundBudget);
        assertEquals(budget.getBudgetId(), foundBudget.getBudgetId());
    }

    @Test
    void testFindByIdThrowsExceptionForInvalidId() {
        // Given
        Long invalidBudgetId = 999L;

        // When / Then
        assertThrows(BudgetNotFoundException.class, () -> budgetService.findById(invalidBudgetId));
    }

    @Test
    @Transactional
    void testGetBudgetById() {
        // When
        BudgetResponse response = budgetService.getBudgetById(budget.getBudgetId());

        // Then
        assertNotNull(response);
        assertEquals(budget.getBudgetId(), response.budgetId());
        assertEquals(BigDecimal.valueOf(1000), response.toSpend());
    }

    @Test
    @Transactional
    void testGetBudgetExpanses() {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Dinner")
                .currency("USD")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(80))
                .exchangeRate(BigDecimal.valueOf(1.0))
                .trip(trip)
                .build();
        expanseRepository.save(expanse);

        // When
        BudgetExpensesRespond budgetExpenses = budgetService.getBudgetExpanses(trip.getTripId(), budget.getBudgetId());

        // Then
        assertNotNull(budgetExpenses);
        assertEquals(1, budgetExpenses.expanses().size());
        assertEquals("Dinner", budgetExpenses.expanses().getFirst().expanseName());
    }
}