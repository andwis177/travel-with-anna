package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.trip.day.Day;
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
public class ActivityAuthorizationService {

    private static final String UNAUTHORIZED_ACTIVITY_MESSAGE =
            "You are not authorized to modify or view this activity";

    private final UserAuthenticationService userService;

    public void authorizeActivities(@NotNull Set<Activity> activities, UserDetails connectedUser) {
        Set<Trip> trips = extractTripsFromActivities(activities);
        verifyAuthorizationWithTrips(trips, connectedUser);
    }

    public void authorizeSingleActivity(@NotNull Activity activity, UserDetails connectedUser) {
        userService.validateOwnership(activity, connectedUser, UNAUTHORIZED_ACTIVITY_MESSAGE);
    }

    private Set<Trip> extractTripsFromActivities(@NotNull Set<Activity> activities) {
        return activities.stream()
                .map(Activity::getDay)
                .map(Day::getTrip)
                .collect(Collectors.toSet());
    }

    private void verifyAuthorizationWithTrips(@NotNull Set<Trip> trips, UserDetails connectedUser) {
        trips.forEach(trip ->
                userService.validateOwnership(trip, connectedUser, UNAUTHORIZED_ACTIVITY_MESSAGE)
        );
    }
}
