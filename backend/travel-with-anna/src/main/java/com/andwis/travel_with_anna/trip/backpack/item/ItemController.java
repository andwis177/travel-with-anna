package com.andwis.travel_with_anna.trip.backpack.item;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
@Tag(name = "Item")
public class ItemController {
    private final ItemFacade itemFacade;

  @GetMapping("/{backpackId}")
    public ResponseEntity<List<ItemRequest>> getAllItemsByBackpackId(@PathVariable("backpackId") Long backpackId) {
      List<ItemRequest> items = itemFacade.getAllItemsByBackpackId(backpackId);
      return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @PatchMapping("/save-list")
    public ResponseEntity<Void> saveAllItemsFromTheList(@RequestBody @Valid List<ItemRequest> items) {
        itemFacade.saveAllItems(items);
        return ResponseEntity.ok().build();
    }
}
