package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DayAuthorizationService {

    private final static String DAY_MSG_EXCEPTION = "You are not authorized to modify or view this day";

    private final UserAuthenticationService userService;

    public void checkDaysAuthorization(@NotNull Set<Day> days, UserDetails connectedUser) {
        days.stream()
                .map(Day::getTrip)
                .forEach(trip -> verifyOwnership(trip, connectedUser));
    }

    public void verifyDayOwner(@NotNull Day day, UserDetails connectedUser) {
        verifyOwnership(day, connectedUser);
    }

    public void verifyTripOwner(@NotNull Trip trip, UserDetails currentUser) {
        verifyOwnership(trip, currentUser);
    }

    private <T extends OwnByUser> void verifyOwnership(@NotNull T entity, UserDetails currentUser) {
        userService.validateOwnership(entity, currentUser, DAY_MSG_EXCEPTION);
    }
}
