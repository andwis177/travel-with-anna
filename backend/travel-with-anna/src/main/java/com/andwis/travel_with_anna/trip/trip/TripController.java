package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("trip")
@RequiredArgsConstructor
@Tag(name = "Trip")
public class TripController {

    private final TripFacade facade;
    private final TripFacade tripFacade;

    @PostMapping("/create")
    public ResponseEntity<Long> createTrip(@RequestBody TripCreatorRequest trip, Authentication connectedUser) {
        Long tripId = facade.createTrip(trip, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripId);
    }

    @GetMapping("")
    public PageResponse<TripDto> getAllOwnersTrips(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser) {
        return tripFacade.getAllOwnersTrips(page, size, connectedUser);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDto> getTripById(@PathVariable("tripId") Long tripId) {
        TripDto tripDto = facade.getTripById(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(tripDto);
    }

    @PatchMapping("/{tripId}/name/change")
    public ResponseEntity<Long> changeTripName(@PathVariable("tripId") Long tripId, @RequestParam String tripName) {
        Long returnedTripId = facade.changeTripName(tripId, tripName);
        return ResponseEntity.status(HttpStatus.OK).body(returnedTripId);
    }


    @PatchMapping("/{tripId}/viewer/{viewerId}/add")
    public ResponseEntity<Void> addViewer(
            @PathVariable("tripId") Long tripId, @PathVariable("viewerId") Long viewerId, Authentication connectedUser) {
        facade.addViewer(tripId, viewerId, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{tripId}/viewer/{viewerId}/remove")
    public ResponseEntity<Void> removeViewer(
            @PathVariable("tripId") Long tripId, @PathVariable("viewerId") Long viewerId) {
        facade.removeViewer(tripId, viewerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tripId}/delete")
    public ResponseEntity<Void> deleteTrip(@PathVariable("tripId") Long tripId, Authentication connectedUser) {
        facade.deleteTrip(tripId, connectedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tripId}/viewers")
    public ResponseEntity<List<ViewerDto>> getTripViewers(@PathVariable("tripId") Long tripId) {
        List<ViewerDto> viewers = facade.getTripViewers(tripId);
        return ResponseEntity.ok(viewers);
    }
}
