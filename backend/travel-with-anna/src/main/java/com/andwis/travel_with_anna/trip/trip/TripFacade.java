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

    public void deleteTrip(TripRequest request, Authentication connectedUser) {
        tripMgr.deleteTrip(request, connectedUser);
    }

    public void updateTrip(TripEditRequest request) {
        tripMgr.updateTrip(request);
    }
}
