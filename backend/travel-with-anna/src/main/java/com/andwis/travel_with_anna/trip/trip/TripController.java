package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trip")
@RequiredArgsConstructor
@Tag(name = "Trip")
public class TripController {

    private final TripFacade facade;

    @PostMapping("/create")
    public ResponseEntity<Long> createTrip(@RequestBody @Valid TripCreatorRequest trip, Authentication connectedUser) {
        Long tripId = facade.createTrip(trip, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripId);
    }

    @GetMapping("")
    public PageResponse<TripResponse> getAllOwnersTrips(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser) {
        return facade.getAllOwnersTrips(page, size, connectedUser);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable("tripId") Long tripId) {
        TripResponse tripDto = facade.getTripById(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(tripDto);
    }

    @PatchMapping("/{tripId}/name/change")
    public ResponseEntity<Long> changeTripName(@PathVariable("tripId") Long tripId, @RequestParam String tripName) {
        Long returnedTripId = facade.changeTripName(tripId, tripName);
        return ResponseEntity.status(HttpStatus.OK).body(returnedTripId);
    }

    @DeleteMapping("/{tripId}/delete")
    public ResponseEntity<Void> deleteTrip(@PathVariable("tripId") Long tripId, Authentication connectedUser) {
        facade.deleteTrip(tripId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
