package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripFacade {

    private final TripMgr tripMgr;

    public Long createTrip(TripCreatorRequest request, UserDetails connectedUser) {
        return tripMgr.createTrip(request, connectedUser);
    }

    public PageResponse<TripResponse> getAllOwnersTrips(int page, int size, UserDetails connectedUser) {
        return tripMgr.getAllOwnersTrips(page, size, connectedUser);
    }

    public TripResponse getTripById(Long tripId, UserDetails connectedUser) {
        return tripMgr.getTripById(tripId, connectedUser);
    }

    public void deleteTrip(TripRequest request, UserDetails connectedUser) throws WrongPasswordException {
        tripMgr.deleteTrip(request, connectedUser);
    }

    public void updateTrip(TripEditRequest request, UserDetails connectedUser) {
        tripMgr.updateTrip(request, connectedUser);
    }
}
