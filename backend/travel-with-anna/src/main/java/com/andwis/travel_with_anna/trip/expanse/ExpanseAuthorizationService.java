package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpanseAuthorizationService {
    private final static String EXPANSE_MSG_EXCEPTION = "You are not authorized to modify or view this expanse";
    private final UserAuthenticationService userService;

    public void verifyTripOwner(@NotNull Trip trip, UserDetails connectedUser) {
        userService.verifyOwner(trip, connectedUser, EXPANSE_MSG_EXCEPTION);
    }

    public void verifyExpanseOwner(@NotNull Expanse expanse, UserDetails connectedUser) {
        userService.verifyOwner(expanse, connectedUser, EXPANSE_MSG_EXCEPTION);
    }
}
