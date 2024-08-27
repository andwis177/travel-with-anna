package com.andwis.travel_with_anna.trip.trip;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;

    public Long saveTrip(Trip trip) {
        return tripRepository.save(trip).getId();
    }
}
