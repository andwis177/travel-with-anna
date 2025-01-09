package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpanseAuthorizationService {
    private final static String AUTHORIZATION_EXCEPTION_MSG = "You are not authorized to access this resource";
    private final UserAuthenticationService userService;

    public void verifyTripOwner(@NotNull Trip trip, UserDetails userDetails) {
        verifyEntityOwner(trip, userDetails);
    }

    public void verifyExpanseOwner(@NotNull Expanse expanse, UserDetails userDetails) {
        verifyEntityOwner(expanse, userDetails);
    }

    private <T extends OwnByUser> void verifyEntityOwner(@NotNull T entity, UserDetails userDetails) {
        userService.validateOwnership(entity, userDetails, AUTHORIZATION_EXCEPTION_MSG);
    }
}
