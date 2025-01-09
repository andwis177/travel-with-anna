package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public void addItemToBackpack(
            @RequestBody @Valid ItemRequest itemRequest,
            @PathVariable("backpackId") Long backpackId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.addItemToBackpack(backpackId, itemRequest, connectedUser);
    }

    @GetMapping("/{backpackId}")
    public ResponseEntity<BackpackResponse> getBackpackById(
            @PathVariable("backpackId") Long backpackId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.getBackpackById(backpackId, connectedUser));
    }

    @DeleteMapping("/{itemId}/item")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @PathVariable("itemId") Long itemId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteItem(itemId, connectedUser);
    }
}
