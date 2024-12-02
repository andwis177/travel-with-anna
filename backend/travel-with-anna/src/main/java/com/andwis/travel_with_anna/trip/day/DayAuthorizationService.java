package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DayAuthorizationService {
    private final static String DAY_MSG_EXCEPTION = "You are not authorized to modify or view this day";
    private final UserAuthenticationService userService;

    public void checkDaysAuthorization(@NotNull Set<Day> days, UserDetails connectedUser) {
        Set<Trip> trips = days.stream().map(Day::getTrip).collect(Collectors.toSet());
        trips.forEach(trip -> userService.verifyOwner(trip, connectedUser, DAY_MSG_EXCEPTION));
    }

    public void verifyDayOwner(@NotNull Day day, UserDetails connectedUser) {
        userService.verifyOwner(day, connectedUser, DAY_MSG_EXCEPTION);
    }

    public void verifyTripOwner(@NotNull Trip trip, UserDetails connectedUser) {
        userService.verifyOwner(trip, connectedUser, DAY_MSG_EXCEPTION);
    }
}
