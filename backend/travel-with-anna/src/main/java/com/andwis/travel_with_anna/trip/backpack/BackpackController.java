package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemCreator;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("backpack")
@Tag(name = "Backpack")
public class BackpackController {

    private final BackpackFacade facade;

    @PatchMapping("/{backpackId}/item-add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addItemToBackpack(
            @RequestBody @Valid ItemCreator itemCreator,
            @PathVariable("backpackId") Long backpackId) {
        facade.addItemToBackpack(backpackId, itemCreator);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/{backpackId}")
    public ResponseEntity<BackpackRequest> getBackpackById(@PathVariable("backpackId") Long backpackId) {
        BackpackRequest backpackDto = facade.getBackpackById(backpackId);
        return ResponseEntity.ok(backpackDto);
    }

    @DeleteMapping("/delete/{itemId}/item")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        facade.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
