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
        List<ItemResponse> items = itemFacade.getAllItemsByBackpackId(backpackId, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @PatchMapping("/save-list")
    public ResponseEntity<Void> saveAllItemsFromTheList(
            @RequestBody List<ItemResponse> items,
            @AuthenticationPrincipal UserDetails connectedUser) {
        itemFacade.saveAllItems(items, connectedUser);
        return ResponseEntity.ok().build();
    }
}
