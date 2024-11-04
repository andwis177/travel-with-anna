package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.utility.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityFacade {

    private final ActivityService service;

    public void createAssociatedActivities(@NotNull @Valid ActivityAssociatedRequest request) {
        service.createAssociatedActivities(request);
    }

    public void createSingleActivity(@Valid ActivityRequest request) {
        service.createSingleActivity(request);
    }

    public MessageResponse updateActivity(ActivityUpdateRequest request) {
       return new MessageResponse(service.updateActivity(request));
    }

    public ActivityDetailedResponse fetchActivitiesByDayId(Long dayId) {
        return service.fetchActivitiesByDayId(dayId);
    }

    public AddressDetail fetchAddressDetailsByDayId(Long activityId) {
        return service.fetchAddressDetailByDayId(activityId);
    }

    public AddressDetail fetchAddressDetailsByTripId(Long tripId) {
        return service.fetchAddressDetailByTripId(tripId);
    }

    public void deleteActivityById(Long activityId) {
        service.deleteActivityById(activityId);
    }
}
