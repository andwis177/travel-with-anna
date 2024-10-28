package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("budget")
@Tag(name = "Budget")
public class BudgetController {
    private final BudgetFacade facade;

    @PostMapping("/create")
    public ResponseEntity<Void> saveBudget(Budget budget) {
        facade.saveBudget(budget);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable("budgetId") Long budgetId) {
        BudgetResponse budgetDto = facade.getBudgetById(budgetId);
        return ResponseEntity.ok(budgetDto);
    }

    @GetMapping("/{budgetId}/expanses/{tripId}")
    public ResponseEntity<BudgetExpensesRespond> getBudgetExpanses(@PathVariable("tripId") Long tripId, @PathVariable("budgetId") Long budgetId) {
        BudgetExpensesRespond budgetExpensesRespond = facade.getBudgetExpanses(tripId, budgetId);
        return ResponseEntity.ok(budgetExpensesRespond);
    }

    @GetMapping("/calculate/{tripId}")
    public ResponseEntity<List<ExpanseTotalByBadge>> getExpansesByBadgeByTripId(@PathVariable("tripId") Long tripId) {
        List<ExpanseTotalByBadge> expanseTotalByType = facade.getExpansesByBadgeByTripId(tripId);
        return ResponseEntity.ok(expanseTotalByType);
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateBudget(@RequestBody @Valid BudgetRequest request) {
        facade.updateBudget(request);
        return ResponseEntity.ok().build();
    }
}
