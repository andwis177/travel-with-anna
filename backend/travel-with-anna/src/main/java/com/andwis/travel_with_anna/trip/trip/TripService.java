package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;

    public Long saveTrip(Trip trip) {
        return tripRepository.save(trip).getTripId();
    }

    public Page<Trip> getTripsByOwnerId(Long userId, Pageable pageable) {
        return tripRepository.findTripsByOwnerId(userId, pageable);
    }

    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("Trip not found"));
    }

    public void deleteTrip(Long tripId) {
        tripRepository.deleteById(tripId);
    }
}
