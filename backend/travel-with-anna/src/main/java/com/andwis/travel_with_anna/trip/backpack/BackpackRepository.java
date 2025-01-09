package com.andwis.travel_with_anna.trip.backpack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BackpackRepository extends JpaRepository<Backpack, Long> {
    @Query("SELECT DISTINCT b FROM Backpack b LEFT JOIN b.backpackItems i WHERE i.itemId IN :itemIds")
    Set<Backpack> findBackpacksByItemIds(@Param("itemIds") List<Long> itemIds);
}
