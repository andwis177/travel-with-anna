package com.andwis.travel_with_anna.trip.day;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayRepository extends JpaRepository<Day, Long> {
    List<Day> findByTripTripIdOrderByDateAsc(Long tripId);
}
