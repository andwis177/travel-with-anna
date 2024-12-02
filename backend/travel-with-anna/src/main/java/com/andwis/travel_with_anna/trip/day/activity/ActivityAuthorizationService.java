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
    private final static String ACTIVITY_MSG_EXCEPTION = "You are not authorized to modify or view this activity";
    private final UserAuthenticationService userService;

    public void checkActivitiesAuthorization(@NotNull Set<Activity> activities, UserDetails connectedUser) {
        Set<Trip> trips = activities.stream().map(Activity::getDay).map(Day::getTrip).collect(Collectors.toSet());
        trips.forEach(trip -> userService.verifyOwner(trip, connectedUser, ACTIVITY_MSG_EXCEPTION));
    }

    public void verifyActivityOwner(@NotNull Activity activity, UserDetails connectedUser) {
        userService.verifyOwner(activity, connectedUser, ACTIVITY_MSG_EXCEPTION);
    }
}
