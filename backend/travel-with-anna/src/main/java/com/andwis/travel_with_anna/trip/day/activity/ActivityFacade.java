package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.utility.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityFacade {

    private final ActivityService service;

    public void createAssociatedActivities(@NotNull ActivityAssociatedRequest request, UserDetails connectedUser) {
        service.createAssociatedActivities(request, connectedUser);
    }

    public void createSingleActivity(@Valid ActivityRequest request, UserDetails connectedUser) {
        service.createSingleActivity(request, connectedUser);
    }

    public MessageResponse updateActivity(ActivityUpdateRequest request, UserDetails connectedUser) {
        return service.updateActivity(request, connectedUser);
    }

    public ActivityDetailedResponse fetchActivitiesByDayId(Long dayId, UserDetails connectedUser) {
        return service.fetchActivitiesByDayId(dayId, connectedUser);
    }

    public AddressDetail fetchAddressDetailsByDayId(Long activityId, UserDetails connectedUser) {
        return service.fetchAddressDetailByDayId(activityId, connectedUser);
    }

    public AddressDetail fetchAddressDetailsByTripId(Long tripId, UserDetails connectedUser) {
        return service.fetchAddressDetailByTripId(tripId, connectedUser);
    }

    public void deleteActivityById(Long activityId, UserDetails connectedUser) {
        service.deleteActivityById(activityId, connectedUser);
    }
}
