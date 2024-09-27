package com.andwis.travel_with_anna.trip.backpack.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

   @Query("SELECT i FROM Item i WHERE i.itemId IN :idList")
   List<Item> findAllById(@Param("idList")List<Long> idList);

   @Query("SELECT i FROM Item i WHERE i.backpack.backpackId = :backpackId ORDER BY i.itemName ASC")
   List<Item> findAllByBackpackId(@Param("backpackId")Long backpackId);

}
