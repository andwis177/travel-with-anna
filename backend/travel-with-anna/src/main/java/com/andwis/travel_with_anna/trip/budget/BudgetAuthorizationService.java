package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetAuthorizationService {

    private static final String UNAUTHORIZED_BUDGET_ACTION_MSG = "You are not authorized to modify or view this budget";

    private final UserAuthenticationService userService;

    public void verifyBudgetOwner(@NotNull UserDetails connectedUser, @NotNull Budget budget) {
        userService.validateOwnership(budget, connectedUser, UNAUTHORIZED_BUDGET_ACTION_MSG);
    }
}
