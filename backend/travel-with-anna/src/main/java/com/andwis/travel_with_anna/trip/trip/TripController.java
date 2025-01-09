package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trip")
@RequiredArgsConstructor
@Tag(name = "Trip")
public class TripController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";

    private final TripFacade facade;

    @PostMapping
    public ResponseEntity<Long> createTrip(
            @RequestBody @Valid TripCreatorRequest trip,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facade.createTrip(trip, connectedUser));
    }

    @GetMapping
    public PageResponse<TripResponse> getAllOwnersTrips(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE,
                    required = false) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE,
                    required = false) int size,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return facade.getAllOwnersTrips(page, size, connectedUser);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(facade.getTripById(tripId, userDetails));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateTrip(
            @RequestBody @Valid TripEditRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.updateTrip(request, connectedUser);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(
            @RequestBody @Valid TripRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        facade.deleteTrip(request, connectedUser);
    }
}
