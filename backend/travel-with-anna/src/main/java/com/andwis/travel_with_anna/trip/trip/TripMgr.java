package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.utility.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripMgr {
    private final TripService tripService;
    private final UserService userService;
    private final DayService dayService;

    public Long createTrip(@NotNull @Valid TripCreatorRequest request, Authentication connectedUser) {
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

        trip.addBackpack(backpack);
        trip.addBudget(budget);

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

    public void deleteTrip(@NotNull TripRequest request, Authentication connectedUser) {
        User adminUser = userService.getConnectedUser(connectedUser);
        userService.verifyPassword(adminUser, request.password());
        Trip trip = tripService.getTripById(request.tripId());
        trip.removeTripAssociations();
        tripService.deleteById(request.tripId());
    }

    public void updateTrip(@NotNull TripEditRequest request) {
        Trip trip = tripService.getTripById(request.getDayGeneratorRequest().getTripId());
        LocalDate startDate = request.getDayGeneratorRequest().getStartDate();
        LocalDate endDate = request.getDayGeneratorRequest().getEndDate();

        trip.setTripName(request.getTripName());

        if (!trip.getDays().getFirst().getDate().equals(startDate)
                || !trip.getDays().getLast().getDate().equals(endDate)) {
            dayService.changeTripDates(
                    trip, startDate, endDate);
        }
        tripService.saveTrip(trip);
    }
}
