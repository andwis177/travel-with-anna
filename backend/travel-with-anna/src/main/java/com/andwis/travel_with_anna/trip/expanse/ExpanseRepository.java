package com.andwis.travel_with_anna.trip.expanse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpanseRepository extends JpaRepository<Expanse, Long> {

    @Query("SELECT e FROM Expanse e WHERE e.trip.tripId = :tripId")
    List<Expanse> findByTripId(Long tripId);
}
