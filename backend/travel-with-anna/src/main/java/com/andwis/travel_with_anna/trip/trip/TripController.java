package com.andwis.travel_with_anna.trip.trip;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trip")
@RequiredArgsConstructor
@Tag(name = "Trip")
public class TripController {
    private final TripService service;

    @PostMapping("/create")
    public ResponseEntity<Void> saveTrip(Trip trip) {
        service.saveTrip(trip);
        return ResponseEntity.accepted().build();
    }
}
