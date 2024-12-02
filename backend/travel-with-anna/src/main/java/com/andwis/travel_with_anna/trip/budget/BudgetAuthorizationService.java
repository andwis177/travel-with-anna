package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetAuthorizationService {
    private final static String BUDGET_MSG_EXCEPTION = "You are not authorized to modify or view this budget";
    private final UserAuthenticationService userService;

    public void verifyBudgetOwner(@NotNull Budget budget, UserDetails connectedUser) {
        userService.verifyOwner(budget, connectedUser, BUDGET_MSG_EXCEPTION);
    }
}
