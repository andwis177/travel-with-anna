package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.user.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BudgetAuthorizationServiceTest {
    private BudgetAuthorizationService budgetAuthorizationService;

    @Mock
    private UserAuthenticationService userService;

    @Mock
    private UserDetails connectedUser;

    @Mock
    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budgetAuthorizationService = new BudgetAuthorizationService(userService);
    }

    @Test
    void verifyBudgetOwner_authorizedUser_callsValidateOwnership() {
        // Given
        doNothing().when(userService)
                .validateOwnership(budget, connectedUser, "You are not authorized to modify or view this budget");

        // When
        budgetAuthorizationService.verifyBudgetOwner(connectedUser, budget);

        // Then
        verify(userService, times(1))
                .validateOwnership(budget, connectedUser, "You are not authorized to modify or view this budget");
    }

    @Test
    void verifyBudgetOwner_unauthorizedUser_throwsException() {
        // Give
        doThrow(new BadCredentialsException("You are not authorized to modify or view this budget"))
                .when(userService).validateOwnership(budget, connectedUser, "You are not authorized to modify or view this budget");

        // When & Then
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            budgetAuthorizationService.verifyBudgetOwner(connectedUser, budget);
        });

        assertEquals("You are not authorized to modify or view this budget", exception.getMessage());
    }
}