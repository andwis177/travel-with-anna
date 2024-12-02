package com.andwis.travel_with_anna.trip.backpack.item;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemFacade {
    private final ItemService itemService;

    public void saveAllItems(List<ItemResponse> items, UserDetails connectedUser) {
        itemService.saveAllItems(items, connectedUser);
    }

    public List<ItemResponse> getAllItemsByBackpackId(Long backpackId, UserDetails connectedUser) {
        return itemService.getAllItemsByBackpackId(backpackId, connectedUser);
    }
}
