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

    private final TripFacade facade;

    @PostMapping
    public ResponseEntity<Long> createTrip(
            @RequestBody @Valid TripCreatorRequest trip,
            @AuthenticationPrincipal UserDetails connectedUser) {
        Long tripId = facade.createTrip(trip, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripId);
    }

    @GetMapping
    public PageResponse<TripResponse> getAllOwnersTrips(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return facade.getAllOwnersTrips(page, size, connectedUser);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {
        TripResponse tripDto = facade.getTripById(tripId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(tripDto);
    }

    @PatchMapping
    public ResponseEntity<Void> updateTrip(
            @RequestBody @Valid TripEditRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.updateTrip(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTrip(
            @RequestBody TripRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        facade.deleteTrip(request, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
