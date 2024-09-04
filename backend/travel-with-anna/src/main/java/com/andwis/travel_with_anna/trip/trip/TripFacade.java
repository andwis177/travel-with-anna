package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
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
import java.util.Set;

import static com.andwis.travel_with_anna.role.Role.getAdminRole;

@Service
@RequiredArgsConstructor
@Transactional
public class TripFacade {
    private final TripService tripService;
    private final UserService userService;
    private final AvatarService avatarService;

    public Long createTrip(TripCreatorRequest request, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);

        Backpack backpack = Backpack.builder()
                .build();

        Budget budget = Budget.builder()
                .currency(request.currency())
                .toSpend(request.toSpend())
                .build();

        Trip trip = Trip.builder()
                .tripName(request.tripName())
                .build();

        trip.setBackpack(backpack);
        trip.setBudget(budget);

        user.addTrip(trip);
        return tripService.saveTrip(trip);
    }

    public PageResponse<TripDto> getAllOwnersTrips(int page, int size, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Trip> trips = tripService.getTripsByOwnerId(user.getUserId(), pageable);
        List<TripDto> tripsDto = trips.stream().map(TripMapper::toTripDto).toList();
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

    public TripDto getTripById(Long tripId) {
        return TripMapper.toTripDto(tripService.getTripById(tripId));
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

    public void addViewer(Long tripId, Long viewerId, Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);
        User viewer = userService.getUserById(viewerId);
        if (user.getUserId().equals(viewerId)) {
            throw new IllegalArgumentException("You can't add yourself as a viewer");
        }
        if (user.getRole().getRoleName().equals(getAdminRole())) {
            throw new IllegalArgumentException("Admin can't be a viewer");
        }
        Trip trip = tripService.getTripById(tripId);
        if (!trip.getViewers().contains(viewer)) {
            trip.addViewer(viewer);
            tripService.saveTrip(trip);
            userService.saveUser(viewer);
        }
    }

    public List<ViewerDto> getTripViewers(Long tripId) {
        Trip trip = tripService.getTripById(tripId);
        Set<User> viewers = trip.getViewers();
        return viewers.stream()
                .map(viewer -> {
                    ViewerDto viewerDto = TripMapper.toViewerDto(viewer);
                    viewerDto.setCover(avatarService.getAvatar(viewer).getAvatar());
                    return viewerDto;
                }).toList();
    }

    public void removeViewer(Long tripId, Long viewerId) {
        User viewer = userService.getUserById(viewerId);
        Trip trip = tripService.getTripById(tripId);
        if (trip.getViewers().contains(viewer)) {
            trip.removeViewer(viewer);
            tripService.saveTrip(trip);
            userService.saveUser(viewer);
        }
    }
}
