package com.andwis.travel_with_anna.trip.backpack.item;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
@Tag(name = "Item")
public class ItemController {
    private final ItemFacade itemFacade;

    @GetMapping("/{backpackId}")
    public ResponseEntity<List<ItemResponse>> getAllItemsByBackpackId(
            @PathVariable("backpackId") Long backpackId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(itemFacade.getAllItemsByBackpackId(backpackId, connectedUser));
    }

    @PatchMapping("/save-list")
    @ResponseStatus(HttpStatus.OK)
    public void saveAllItemsFromTheList(
            @RequestBody List<ItemResponse> items,
            @AuthenticationPrincipal UserDetails connectedUser) {
        itemFacade.saveAllItems(items, connectedUser);
    }
}
