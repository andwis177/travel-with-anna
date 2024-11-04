package com.andwis.travel_with_anna.trip.day;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DayRepository extends JpaRepository<Day, Long> {

    Optional<Day> findByTripTripIdAndDate(Long tripId, LocalDate date);

    Set<Day> findByTripTripId(Long tripId);

    List<Day> findByTripTripIdOrderByDateAsc(Long tripId);

    Set<Day> findByTripTripIdIn(Set<Long> tripIds);
}
