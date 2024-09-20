package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemWithExpanseRequest;

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
            @RequestBody @Valid ItemWithExpanseRequest itemWithExpanseRequest,
            @PathVariable("backpackId") Long backpackId) {
        facade.addItemToBackpack(backpackId, itemWithExpanseRequest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/{backpackId}")
    public ResponseEntity<BackpackResponse> getBackpackById(@PathVariable("backpackId") Long backpackId) {
        BackpackResponse response = facade.getBackpackById(backpackId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{itemId}/item")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        facade.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
