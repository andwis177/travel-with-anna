package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("budget")
@Tag(name = "Budget")
public class BudgetController {

    private final BudgetFacade facade;

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable("budgetId") Long budgetId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return createOkResponse(facade.getBudgetById(budgetId, connectedUser));
    }

    @GetMapping("/{budgetId}/expanses/{tripId}")
    public ResponseEntity<BudgetExpensesRespond> getBudgetExpanses(
            @PathVariable("tripId") Long tripId,
            @PathVariable("budgetId") Long budgetId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return createOkResponse(facade.getBudgetExpanses(tripId, budgetId, connectedUser));
    }

    @GetMapping("/calculate/{tripId}")
    public ResponseEntity<List<ExpanseTotalByBadge>> getExpansesByBadgeByTripId(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return createOkResponse(facade.getExpansesByBadgeByTripId(tripId, connectedUser));
    }

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateBudget(
            @RequestBody @Valid BudgetRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.updateBudget(request, connectedUser);
    }

    private <T> @NotNull ResponseEntity<T> createOkResponse(T body) {
        return ResponseEntity.ok(body);
    }
}
