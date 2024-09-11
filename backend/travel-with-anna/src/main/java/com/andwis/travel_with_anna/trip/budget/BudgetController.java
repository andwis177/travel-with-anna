package com.andwis.travel_with_anna.trip.budget;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BudgetRequest> getBudgetById(@PathVariable("budgetId") Long budgetId) {
        BudgetRequest budgetDto = facade.getBudgetById(budgetId);
        return ResponseEntity.ok(budgetDto);
    }
}
