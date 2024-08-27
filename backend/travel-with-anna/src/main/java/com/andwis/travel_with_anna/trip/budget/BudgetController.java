package com.andwis.travel_with_anna.trip.budget;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("budget")
@Tag(name = "Budget")
public class BudgetController {
    private final BudgetService service;

    @PostMapping("/create")
    public ResponseEntity<Void> saveBudget(Budget budget) {
        service.saveBudget(budget);
        return ResponseEntity.accepted().build();
    }
}
