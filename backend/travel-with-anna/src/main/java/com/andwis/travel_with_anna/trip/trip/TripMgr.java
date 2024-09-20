package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripMgr {
    private final TripService tripService;
    private final UserService userService;

    public Long createTrip(TripCreatorRequest request, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);

        Backpack backpack = Backpack.builder()
                .build();

        Budget budget = Budget.builder()
                .currency(request.getCurrency())
                .toSpend(request.getToSpend())
                .build();

        Trip trip = Trip.builder()
                .tripName(request.getTripName())
                .build();

        trip.setBackpack(backpack);
        trip.setBudget(budget);

        user.addTrip(trip);
        return tripService.saveTrip(trip);
    }

    public PageResponse<TripResponse> getAllOwnersTrips(int page, int size, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Trip> trips = tripService.getTripsByOwnerId(user.getUserId(), pageable);
        List<TripResponse> tripsDto = trips.stream().map(TripMapper::toTripResponse).toList();
        return new PageResponse<>(
                tripsDto,
                trips.getNumber(),
                trips.getSize(),
                trips.getTotalElements(),
                trips.getTotalPages(),
                trips.isFirst(),
                trips.isLast()
        );
    }

    public TripResponse getTripById(Long tripId) {
        return TripMapper.toTripResponse(tripService.getTripById(tripId));
    }

    public Long changeTripName(Long tripId, String tripName) {
        Trip trip = tripService.getTripById(tripId);
        trip.setTripName(tripName);
        return tripService.saveTrip(trip);
    }

    public void deleteTrip(Long tripId, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);
        Trip trip = tripService.getTripById(tripId);
        user.removeTrip(trip);
        userService.saveUser(user);
        tripService.deleteTrip(tripId);
    }
}
