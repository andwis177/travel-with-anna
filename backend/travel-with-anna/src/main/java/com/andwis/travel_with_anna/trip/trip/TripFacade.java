package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripFacade {
    private final TripMgr tripMgr;

    public Long createTrip(TripCreatorRequest request, Authentication connectedUser) {
        return tripMgr.createTrip(request, connectedUser);
    }

    public PageResponse<TripResponse> getAllOwnersTrips(int page, int size, Authentication connectedUser) {
        return tripMgr.getAllOwnersTrips(page, size, connectedUser);
    }

    public TripResponse getTripById(Long tripId) {
        return tripMgr.getTripById(tripId);
    }

    public Long changeTripName(Long tripId, String tripName) {
        return tripMgr.changeTripName(tripId, tripName);
    }

    public void deleteTrip(Long tripId, Authentication connectedUser) {
        tripMgr.deleteTrip(tripId, connectedUser);
    }
}
