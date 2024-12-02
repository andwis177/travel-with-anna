package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("backpack")
@Tag(name = "Backpack")
public class BackpackController {
    private final BackpackFacade facade;

    @PatchMapping("/{backpackId}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addItemToBackpack(
            @RequestBody @Valid ItemRequest itemRequest,
            @PathVariable("backpackId") Long backpackId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.addItemToBackpack(backpackId, itemRequest, connectedUser);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping("/{backpackId}")
    public ResponseEntity<BackpackResponse> getBackpackById(
            @PathVariable("backpackId") Long backpackId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        BackpackResponse response = facade.getBackpackById(backpackId, connectedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}/item")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("itemId") Long itemId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteItem(itemId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
