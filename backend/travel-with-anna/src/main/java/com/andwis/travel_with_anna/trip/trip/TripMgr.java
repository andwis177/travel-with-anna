package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.address.AddressService;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.backpack.BackpackService;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import com.andwis.travel_with_anna.utility.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripMgr {
    private final TripService tripService;
    private final UserAuthenticationService userAuthenticationService;
    private final DayService dayService;
    private final AddressService addressService;
    private final BackpackService backpackService;

    @Transactional
    public Long createTrip(@NotNull @Valid TripCreatorRequest request, UserDetails connectedUser) {
        User user = userAuthenticationService.getConnectedUser(connectedUser);

        Backpack backpack = Backpack.builder()
                .items(new ArrayList<>())
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

        Long tripId =  tripService.saveTrip(trip);

        ItemRequest itemRequest = ItemRequest.builder()
                .build();

        backpackService.addItemToBackpack(tripId, itemRequest, connectedUser);
        return tripId;
    }

    public PageResponse<TripResponse> getAllOwnersTrips(int page, int size, UserDetails connectedUser) {
        User user = userAuthenticationService.getConnectedUser(connectedUser);
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

    public TripResponse getTripById(Long tripId, UserDetails connectedUser) {
        Trip trip = tripService.getTripById(tripId);
        userAuthenticationService.verifyOwner(trip.getOwner(), connectedUser, "");
        return TripMapper.toTripResponse(trip);
    }

    @Transactional
    public void deleteTrip(
            @NotNull TripRequest request, UserDetails connectedUser)
            throws WrongPasswordException {
        User adminUser = userAuthenticationService.getConnectedUser(connectedUser);
        userAuthenticationService.verifyPassword(adminUser, request.password());
        Trip trip = tripService.getTripById(request.tripId());
        userAuthenticationService.verifyOwner(trip.getOwner(), connectedUser, "");
        trip.removeTripAssociations();
        tripService.delete(trip);
        addressService.deleteAllByAddressIdIn(getAllTripAddresses(trip));
    }

    private Set<Long> getAllTripAddresses(@NotNull Trip trip) {
        Set<Day> days = trip.getDays();
        Set<Activity> activities = days.stream().map(Day::getActivities).flatMap(Set::stream).collect(Collectors.toSet());
        return activities.stream().map(Activity::getAddress).map(Address::getAddressId).collect(Collectors.toSet());
    }

    @Transactional
    public void updateTrip(@NotNull TripEditRequest request, UserDetails connectedUser) {
        Long tripId = request.getDayGeneratorRequest().getTripId();
        Trip trip = tripService.getTripById(tripId);
        userAuthenticationService.verifyOwner(trip.getOwner(), connectedUser, "You are not authorized to edit this trip");
        LocalDate startDate = request.getDayGeneratorRequest().getStartDate();
        LocalDate endDate = request.getDayGeneratorRequest().getEndDate();

        trip.setTripName(request.getTripName());

        if (!trip.getDaysInOrder().getFirst().getDate().equals(startDate)
                || !trip.getDaysInOrder().getLast().getDate().equals(endDate)) {
            dayService.changeTripDates(
                    trip, startDate, endDate);
        }
        tripService.saveTrip(trip);
    }
}
