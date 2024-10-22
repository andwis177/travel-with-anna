package com.andwis.travel_with_anna.trip.day;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long> {

    Optional<Day> findByTripTripIdAndDate(Long tripId, LocalDate date);

    List<Day> findByTripTripIdOrderByDateAsc(Long tripId);
}
